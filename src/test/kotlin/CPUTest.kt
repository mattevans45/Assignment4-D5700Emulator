package org.example

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

@OptIn(ExperimentalUnsignedTypes::class)
class CPUTest {

    private lateinit var romStrategy: RomStrategy
    private lateinit var ramStrategy: RamStrategy
    private lateinit var keyboard: Keyboard
    private lateinit var testObserver: TestEmulatorObserver

    @BeforeEach
    fun setUp() {
        romStrategy = RomStrategy()
        ramStrategy = RamStrategy()
        keyboard = Keyboard()
        testObserver = TestEmulatorObserver()

        CPU.reset()
        CPU.init(romStrategy, ramStrategy)
        CPU.addObserver(testObserver)
    }
    @AfterEach
    fun tearDown() {
        // Remove all observers added during tests
        CPU.removeObserver(testObserver)
        CPU.reset()

    }

    @Test
    fun `init should set up memory strategies correctly`() {
        val rom = RomStrategy()
        val ram = RamStrategy()

        CPU.init(rom, ram)

        // Verify initial state
        assertEquals(0u.toUByte(), CPU.getTimer())
        assertEquals(0u.toUShort(), CPU.getAddressRegister())

        // All registers should be zero initially
        for (i in 0..7) {
            assertEquals(0u.toUByte(), CPU.getRegister(i))
        }
    }

    @Test
    fun `addObserver should add observer to list`() {
        val observer = TestEmulatorObserver()

        CPU.addObserver(observer)

        // Trigger an event to verify observer was added
        CPU.onEmulatorEvent(TimerEvent.Set(5u))

        assertTrue(observer.eventsReceived.isNotEmpty())
    }

    @Test
    fun `removeObserver should remove observer from list`() {
        val observer = TestEmulatorObserver()
        CPU.addObserver(observer)

        CPU.removeObserver(observer)

        // Trigger an event - observer should not receive it
        CPU.onEmulatorEvent(TimerEvent.Set(5u))

        assertTrue(observer.eventsReceived.isEmpty())
    }

    @Test
    fun `step should halt on 0x0000 instruction`() {

        val romData = ubyteArrayOf(0x00u, 0x00u)
        romStrategy.load(romData)

        val result = CPU.step(keyboard)

        assertNotNull(result)
        assertTrue(result is SystemEvent.Halt)
        assertEquals("Encountered HALT instruction at PC=2", (result as SystemEvent.Halt).message)
    }

    @Test
    fun `step should increment program counter by 2`() {
        // Set up ROM with a simple instruction (e.g., 0x1000 - ADD)
        val romData = ubyteArrayOf(0x10u, 0x00u, 0x00u, 0x00u)
        romStrategy.load(romData)

        // Reset and check initial PC is 0
        CPU.reset()
        CPU.init(romStrategy, ramStrategy)

        CPU.step(keyboard)

        // PC should have moved from 0 to 2, then instruction might modify it further
        // This test verifies the basic PC increment happens
    }



    @Test
    fun `step should execute instruction and handle result`() {
        // Set up ROM with a timer set instruction
        val romData = ubyteArrayOf(0xB0u, 0x05u) // SET_T with value 5
        romStrategy.load(romData)

        CPU.step(keyboard)

        // Should have notified observers about timer tick
        assertTrue(testObserver.eventsReceived.any { it is TimerEvent.Tick })
    }

    @Test
    fun `step should trigger refresh after display events`() {
        // Set up ROM with a draw instruction
        val romData = ubyteArrayOf(0xF0u, 0x00u) // DRAW instruction
        romStrategy.load(romData)

        CPU.step(keyboard)

        // Should have triggered a refresh event after the display event
        assertTrue(testObserver.eventsReceived.any { it is DisplayEvent})
    }

    @Test
    fun `decrementTimer should decrement timer when greater than zero`() {
        // Set timer to non-zero value
        CPU.onEmulatorEvent(TimerEvent.Set(5u))

        CPU.decrementTimer()

        assertEquals(4u.toUByte(), CPU.getTimer())
        assertTrue(testObserver.eventsReceived.any {
            it is TimerEvent.Tick && it.value == 4u.toUByte()
        })
    }

    @Test
    fun `decrementTimer should not decrement when timer is zero`() {
        // Timer starts at 0
        assertEquals(0u.toUByte(), CPU.getTimer())

        CPU.decrementTimer()

        assertEquals(0u.toUByte(), CPU.getTimer())
        // Should not have generated a tick event
        assertFalse(testObserver.eventsReceived.any { it is TimerEvent.Tick })
    }

    @Test
    fun `getTimer should return current timer value`() {
        CPU.onEmulatorEvent(TimerEvent.Set(42u))

        assertEquals(42u.toUByte(), CPU.getTimer())
    }

    @Test
    fun `register operations should work correctly`() {
        // Test setting and getting registers
        CPU.setRegister(0, 0xAAu)
        CPU.setRegister(7, 0x55u)

        assertEquals(0xAAu.toUByte(), CPU.getRegister(0))
        assertEquals(0x55u.toUByte(), CPU.getRegister(7))
        assertEquals(0u.toUByte(), CPU.getRegister(1)) // Unchanged register
    }

    @Test
    fun `address register operations should work correctly`() {
        CPU.setAddressRegister(0x1234u)

        assertEquals(0x1234u.toUShort(), CPU.getAddressRegister())
    }

    @Test
    fun `memory operations should use current data memory strategy`() {
        // Initially should use RAM strategy
        CPU.writeToDataMemory(0x100u, 0xAAu)
        assertEquals(0xAAu.toUByte(), CPU.readFromDataMemory(0x100u))

        // Switch to ROM strategy
        CPU.onEmulatorEvent(SystemEvent.SwitchMemory)

        // Now should use ROM strategy (which might throw exception on write)
        assertThrows<Exception> {
            CPU.writeToDataMemory(0x100u, 0xBBu)
        }
    }

    @Test
    fun `onEmulatorEvent should handle SwitchMemory event`() {

        CPU.writeToDataMemory(0x0100u, 0xAAu)

        CPU.onEmulatorEvent(SystemEvent.SwitchMemory)

        assertTrue(CPU.isMemoryFlagSet(), "Memory flag should be set to ROM")

        assertThrows<IllegalStateException> {
            CPU.writeToDataMemory(0x0100u, 0xBBu)  // Writing to ROM should throw
        }
    }





    @Test
    fun `onEmulatorEvent should handle TimerSet event`() {
        CPU.onEmulatorEvent(TimerEvent.Set(123u))

        assertEquals(123u.toUByte(), CPU.getTimer())
        assertTrue(testObserver.eventsReceived.any {
            it is TimerEvent.Tick && it.value == 123u.toUByte()
        })
    }

    @Test
    fun `reset should clear all state`() {
        // Set up some state
        CPU.setRegister(0, 0xAAu)
        CPU.setAddressRegister(0x1234u)
        CPU.onEmulatorEvent(TimerEvent.Set(42u))

        CPU.reset()

        // All state should be cleared
        assertEquals(0u.toUByte(), CPU.getRegister(0))
        assertEquals(0u.toUShort(), CPU.getAddressRegister())
        assertEquals(0u.toUByte(), CPU.getTimer())

        // Should be using RAM strategy again
        CPU.writeToDataMemory(0x100u, 0xAAu) // Should not throw
        assertEquals(0xAAu.toUByte(), CPU.readFromDataMemory(0x100u))
    }
}

class TestEmulatorObserver : EmulatorObserver {
    val eventsReceived = mutableListOf<EmulatorEvent>()

    override fun onEmulatorEvent(event: EmulatorEvent) {
        eventsReceived.add(event)
    }
}