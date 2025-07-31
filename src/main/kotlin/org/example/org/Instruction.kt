package org.example

abstract class Instruction {
    // The execute method is the template method.
    fun execute(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): ExecutionResult {
        val event = performOperation(cpu, keyboard, display)
        val pcAction = updateProgramCounter(cpu)
        return ExecutionResult(pcAction, event)
    }

    // Steps to be implemented by subclasses
    protected abstract fun performOperation(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent?
    protected open fun updateProgramCounter(cpu: CPU): PcAction = NoChange
}
