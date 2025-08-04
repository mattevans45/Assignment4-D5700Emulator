package org.example

enum class EmulatorState {
    UNINITIALIZED, READY, RUNNING, PAUSED, HALTED;

    fun canTransitionTo(newState: EmulatorState): Boolean = when (this) {
        UNINITIALIZED -> newState == READY
        READY -> newState in setOf(RUNNING, HALTED)
        RUNNING -> newState in setOf(PAUSED, HALTED)
        PAUSED -> newState in setOf(RUNNING, HALTED)
        HALTED -> newState == READY
    }
}
