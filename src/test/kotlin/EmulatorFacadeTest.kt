package org.example


import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFailsWith

class EmulatorFacadeTest {

    private lateinit var facade: EmulatorFacade

    @BeforeEach
    fun setup() {
        facade = EmulatorFacade()
    }

    private fun setState(newState: EmulatorState) {
        val field = EmulatorFacade::class.java.getDeclaredField("state")
        field.isAccessible = true
        field.set(facade, newState)
    }

    @Test
    fun `start throws when not READY`() {
        setState(EmulatorState.UNINITIALIZED)
        assertFailsWith<IllegalStateException> {
            facade.start()
        }
    }

    @Test
    fun `start sets state to RUNNING when READY`() {
        setState(EmulatorState.READY)
        facade.start()
        assertEquals(EmulatorState.RUNNING, facade.getState())
        facade.shutdown() // cleanup threads after test
    }

    @Test
    fun `pause and resume change state correctly`() {
        setState(EmulatorState.RUNNING)
        facade.pause()
        assertEquals(EmulatorState.PAUSED, facade.getState())

        facade.resume()
        assertEquals(EmulatorState.RUNNING, facade.getState())
    }

    @Test
    fun `pause throws if invalid transition`() {
        setState(EmulatorState.UNINITIALIZED)
        // pause from UNINITIALIZED is invalid, state remains same
        facade.pause()
        assertEquals(EmulatorState.UNINITIALIZED, facade.getState())
    }

    @Test
    fun `shutdown transitions to HALTED`() {
        setState(EmulatorState.READY)
        facade.shutdown()
        assertEquals(EmulatorState.HALTED, facade.getState())
    }
}
