package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows


@OptIn(ExperimentalUnsignedTypes::class)
class InstructionFactoryTest {

    @Test
    fun `createInstruction should return StoreInstruction for opcode 0x0`() {
        val instruction = InstructionFactory.createInstruction(0x0123u)
        assertTrue(instruction is StoreInstruction)

        val storeInst = instruction as StoreInstruction
        assertEquals(1, storeInst.rX) // Register 1
        assertEquals(0x23u.toUByte(), storeInst.bb) // Value 0x23
    }

    @Test
    fun `createInstruction should return AddInstruction for opcode 0x1`() {
        val instruction = InstructionFactory.createInstruction(0x1234u)
        assertTrue(instruction is AddInstruction)

        val addInst = instruction as AddInstruction
        assertEquals(2, addInst.rX) // Register 2
        assertEquals(3, addInst.rY) // Register 3
        assertEquals(4, addInst.rZ) // Register 4
    }

    @Test
    fun `createInstruction should return SubInstruction for opcode 0x2`() {
        val instruction = InstructionFactory.createInstruction(0x2345u)
        assertTrue(instruction is SubInstruction)

        val subInst = instruction as SubInstruction
        assertEquals(3, subInst.rX)
        assertEquals(4, subInst.rY)
        assertEquals(5, subInst.rZ)
    }

    @Test
    fun `createInstruction should return JumpInstruction for opcode 0x5`() {
        val instruction = InstructionFactory.createInstruction(0x5678u)
        assertTrue(instruction is JumpInstruction)

        val jumpInst = instruction as JumpInstruction
        assertEquals(0x678u.toUShort(), jumpInst.aaa)
    }

    @Test
    fun `createInstruction should return SetTInstruction for opcode 0xB`() {
        val instruction = InstructionFactory.createInstruction(0xB123u)
        assertTrue(instruction is SetTInstruction)

        val setTInst = instruction as SetTInstruction
        assertEquals(0x12u.toUByte(), setTInst.bb) // Middle byte
    }

    @Test
    fun `createInstruction should return DrawInstruction for opcode 0xF`() {
        val instruction = InstructionFactory.createInstruction(0xF123u)
        assertTrue(instruction is DrawInstruction)

        val drawInst = instruction as DrawInstruction
        assertEquals(1, drawInst.rX)
        assertEquals(2, drawInst.rY)
        assertEquals(3, drawInst.rZ)
    }

    @Test
    fun `createInstruction should handle SetAInstruction for opcode 0xA`() {
        val instruction = InstructionFactory.createInstruction(0xA123u)
        assertTrue(instruction is SetAInstruction)

        val setAInst = instruction as SetAInstruction
        assertEquals(0x123u.toUShort(), setAInst.aaa)
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
class AddInstructionTest {

    private lateinit var instruction: AddInstruction

    @BeforeEach
    fun setUp() {
        instruction = AddInstruction(1, 2, 3) // Add register 1 and 2, store in register 3
    }

    @Test
    fun `instruction parameters should be set correctly`() {
        assertEquals(1, instruction.rX)
        assertEquals(2, instruction.rY)
        assertEquals(3, instruction.rZ)
    }

    @Test
    fun `instruction should be created correctly from instruction word`() {
        val instruction = InstructionFactory.createInstruction(0x1123u) as AddInstruction
        assertEquals(1, instruction.rX)
        assertEquals(2, instruction.rY)
        assertEquals(3, instruction.rZ)
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
class ConvertByteToAsciiInstructionTest {

    private lateinit var instruction: ConvertByteToAsciiInstruction

    @BeforeEach
    fun setUp() {
        instruction = ConvertByteToAsciiInstruction(1, 2) // Convert register 1 to ASCII in register 2
    }

    @Test
    fun `instruction parameters should be set correctly`() {
        assertEquals(1, instruction.rX)
        assertEquals(2, instruction.rY)
    }

    @Test
    fun `instruction should be created correctly from instruction word`() {
        val instruction = InstructionFactory.createInstruction(0xE123u) as ConvertByteToAsciiInstruction
        assertEquals(1, instruction.rX)
        assertEquals(2, instruction.rY)
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
class ConvertToBase10InstructionTest {

    private lateinit var instruction: ConvertToBase10Instruction

    @BeforeEach
    fun setUp() {
        instruction = ConvertToBase10Instruction(1) // Convert register 1 to base 10
    }

    @Test
    fun `instruction parameters should be set correctly`() {
        assertEquals(1, instruction.rX)
    }

    @Test
    fun `instruction should be created correctly from instruction word`() {
        val instruction = InstructionFactory.createInstruction(0xD100u) as ConvertToBase10Instruction
        assertEquals(1, instruction.rX)
    }
}

// Integration test to verify instruction factory bit extraction
@OptIn(ExperimentalUnsignedTypes::class)
class InstructionFactoryBitExtractionTest {

    @Test
    fun `bit extraction should work correctly for complex instruction`() {
        // Test instruction: 0xF789 (opcode=F, rX=7, rY=8, rZ=9)
        val instruction = InstructionFactory.createInstruction(0xF789u) as DrawInstruction

        assertEquals(7, instruction.rX)
        assertEquals(8, instruction.rY)
        assertEquals(9, instruction.rZ)
    }

    @Test
    fun `address extraction should work correctly`() {
        // Test instruction: 0x5ABC (opcode=5, address=0xABC)
        val instruction = InstructionFactory.createInstruction(0x5ABCu) as JumpInstruction

        assertEquals(0xABCu.toUShort(), instruction.aaa)
    }

    @Test
    fun `low byte extraction should work correctly`() {
        // Test instruction: 0x01CD (opcode=0, rX=1, lowByte=0xCD)
        val instruction = InstructionFactory.createInstruction(0x01CDu) as StoreInstruction

        assertEquals(1, instruction.rX)
        assertEquals(0xCDu.toUByte(), instruction.bb)
    }

    @Test
    fun `middle byte extraction should work correctly`() {
        // Test instruction: 0xB1CD (opcode=B, middleByte=0x1C)
        val instruction = InstructionFactory.createInstruction(0xB1CDu) as SetTInstruction

        assertEquals(0x1Cu.toUByte(), instruction.bb)
    }
}



@OptIn(ExperimentalUnsignedTypes::class)
class InstructionTest {

    private lateinit var cpu: CPU
    private lateinit var ram: RamStrategy
    private lateinit var rom: RomStrategy
    private lateinit var keyboard: Keyboard

    @BeforeEach
    fun setUp() {
        rom = RomStrategy()
        ram = RamStrategy()
        keyboard = Keyboard()
        CPU.reset()
        CPU.init(rom, ram)
        cpu = CPU
    }

    @Test
    fun `WriteInstruction writes register value to data memory`() {
        cpu.setAddressRegister(0x0100u)
        cpu.setRegister(2, 0xABu)

        WriteInstruction(2).execute(cpu, keyboard)

        assertEquals(0xABu.toUByte(), cpu.readFromDataMemory(0x0100u))
    }

    @Test
    fun `SwitchMemoryInstruction returns SwitchMemory event`() {
        val event = SwitchMemoryInstruction().execute(cpu, keyboard).event
        assertTrue(event is SystemEvent.SwitchMemory)
    }

    @Test
    fun `SkipIfNotEqualInstruction increments PC by 4 when registers differ`() {
        cpu.setRegister(1, 0x01u)
        cpu.setRegister(2, 0x02u)

        val result = SkipIfNotEqualInstruction(1, 2).execute(cpu, keyboard)
        assertEquals(Increment(4), result.pcAction)
    }

    @Test
    fun `SkipIfNotEqualInstruction increments PC by 2 when registers equal`() {
        cpu.setRegister(1, 0x01u)
        cpu.setRegister(2, 0x01u)

        val result = SkipIfNotEqualInstruction(1, 2).execute(cpu, keyboard)
        assertEquals(Increment(2), result.pcAction)
    }

    @Test
    fun `SkipIfEqualInstruction increments PC by 2 when registers equal`() {
        cpu.setRegister(1, 0x05u)
        cpu.setRegister(2, 0x05u)

        val result = SkipIfEqualInstruction(1, 2).execute(cpu, keyboard)
        assertEquals(Increment(2), result.pcAction)
    }

    @Test
    fun `SkipIfEqualInstruction has NoChange when registers differ`() {
        cpu.setRegister(1, 0x05u)
        cpu.setRegister(2, 0x06u)

        val result = SkipIfEqualInstruction(1, 2).execute(cpu, keyboard)
        assertEquals(NoChange, result.pcAction)
    }

    @Test
    fun `ReadTInstruction loads timer into register`() {
        cpu.onEmulatorEvent(TimerEvent.Set(42u))

        ReadTInstruction(3).execute(cpu, keyboard)

        assertEquals(42u.toUByte(), cpu.getRegister(3))
    }

    @Test
    fun `ReadKeyboardInstruction loads keyboard input into register`() {
        val testKeyboard = object : Keyboard() {
            override fun readHexInput(): UByte = 0x0Fu
        }

        ReadKeyboardInstruction(4).execute(cpu, testKeyboard)

        assertEquals(0x0Fu.toUByte(), cpu.getRegister(4))
    }

    @Test
    fun `ReadInstruction loads memory value into register`() {
        cpu.setAddressRegister(0x0200u)
        cpu.writeToDataMemory(0x0200u, 0x77u)

        ReadInstruction(5).execute(cpu, keyboard)

        assertEquals(0x77u.toUByte(), cpu.getRegister(5))
    }


    @Test
    fun `execute should return SetPc for even address`() {
        val cpu = CPU
        cpu.reset()
        val keyboard = Keyboard()

        val instruction = JumpInstruction(0x100u)

        val result = instruction.execute(cpu, keyboard)

        assertTrue(result.pcAction is SetPc)
        assertEquals(0x100u.toUShort(), (result.pcAction as SetPc).address)
        assertNull(result.event)
    }


    @Test
    fun `execute should throw for odd address`() {
        val cpu = CPU
        cpu.reset()
        val keyboard = Keyboard()

        val instruction = JumpInstruction(0x101u)

        val ex = assertThrows<IllegalStateException> {
            instruction.execute(cpu, keyboard)
        }
        assertEquals("JUMP address must be even. Got 257", ex.message)
    }

    @Test
    fun `ConvertByteToAsciiInstruction should convert 0-9 to ASCII digits`() {
        cpu.setRegister(0, 5u) // value in rX
        val instruction = ConvertByteToAsciiInstruction(0, 1)

        val result = instruction.execute(cpu, keyboard)

        assertEquals((48 + 5).toUByte(), cpu.getRegister(1))
        assertNull(result.event)
        assertTrue(result.pcAction is NoChange)
    }

    @Test
    fun `ConvertByteToAsciiInstruction should convert A-F to ASCII letters`() {
        cpu.setRegister(0, 0xC.toUByte()) // 12 decimal
        val instruction = ConvertByteToAsciiInstruction(0, 1)

        instruction.execute(cpu, keyboard)

        assertEquals('C'.code.toUByte(), cpu.getRegister(1))
    }

    @Test
    fun `ConvertByteToAsciiInstruction should throw on value above 0xF`() {
        cpu.setRegister(0, 0x10u) // 16 decimal
        val instruction = ConvertByteToAsciiInstruction(0, 1)

        val ex = assertThrows(IllegalStateException::class.java) {
            instruction.execute(cpu, keyboard)
        }
        assertTrue(ex.message!!.contains("Value for ASCII conversion must be 0-F"))
    }

    @Test
    fun `ConvertToBase10Instruction should split value into hundreds tens ones`() {
        cpu.setRegister(0, 254u) // 254 => hundreds=2, tens=5, ones=4
        cpu.setAddressRegister(0x200u)
        val instruction = ConvertToBase10Instruction(0)

        instruction.execute(cpu, keyboard)

        assertEquals(2u.toUByte(), cpu.readFromDataMemory(0x200u))
        assertEquals(5u.toUByte(), cpu.readFromDataMemory(0x201u))
        assertEquals(4u.toUByte(), cpu.readFromDataMemory(0x202u))
    }

    @Test
    fun `SetAInstruction should set address register`() {
        val instruction = SetAInstruction(0x345u)

        instruction.execute(cpu, keyboard)

        assertEquals(0x345u.toUShort(), cpu.getAddressRegister())
    }

    @Test
    fun `StoreInstruction should set register to given byte`() {
        val instruction = StoreInstruction(2, 0xABu)

        instruction.execute(cpu, keyboard)

        assertEquals(0xABu.toUByte(), cpu.getRegister(2))
    }

    @Test
    fun `SubInstruction should subtract and store result`() {
        cpu.setRegister(0, 15u)
        cpu.setRegister(1, 5u)
        val instruction = SubInstruction(0, 1, 2)

        instruction.execute(cpu, keyboard)

        assertEquals(10u.toUByte(), cpu.getRegister(2))
    }

    @Test
    fun `SubInstruction should not go below zero`() {
        cpu.setRegister(0, 5u)
        cpu.setRegister(1, 10u)
        val instruction = SubInstruction(0, 1, 2)

        instruction.execute(cpu, keyboard)

        assertEquals(0u.toUByte(), cpu.getRegister(2))
    }
}



