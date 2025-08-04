package org.example

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EmulatorStateTest {

    @Test
    fun `valid state transitions`() {
        assertTrue(EmulatorState.UNINITIALIZED.canTransitionTo(EmulatorState.READY))
        assertTrue(EmulatorState.READY.canTransitionTo(EmulatorState.RUNNING))
        assertTrue(EmulatorState.READY.canTransitionTo(EmulatorState.HALTED))
        assertTrue(EmulatorState.RUNNING.canTransitionTo(EmulatorState.PAUSED))
        assertTrue(EmulatorState.RUNNING.canTransitionTo(EmulatorState.HALTED))
        assertTrue(EmulatorState.PAUSED.canTransitionTo(EmulatorState.RUNNING))
        assertTrue(EmulatorState.PAUSED.canTransitionTo(EmulatorState.HALTED))
        assertTrue(EmulatorState.HALTED.canTransitionTo(EmulatorState.READY))
    }

    @Test
    fun `invalid state transitions`() {
        assertFalse(EmulatorState.UNINITIALIZED.canTransitionTo(EmulatorState.RUNNING))
        assertFalse(EmulatorState.UNINITIALIZED.canTransitionTo(EmulatorState.PAUSED))
        assertFalse(EmulatorState.READY.canTransitionTo(EmulatorState.PAUSED))
        assertFalse(EmulatorState.RUNNING.canTransitionTo(EmulatorState.READY))
        assertFalse(EmulatorState.PAUSED.canTransitionTo(EmulatorState.READY))
        assertFalse(EmulatorState.HALTED.canTransitionTo(EmulatorState.RUNNING))
    }
}
