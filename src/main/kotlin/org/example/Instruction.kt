package org.example

abstract class Instruction {
    fun execute(cpu: CPU, keyboard: Keyboard): InstructionResult {
        val event = performOperation(cpu, keyboard)
        val pcAction = updateProgramCounter(cpu)
        return InstructionResult(pcAction, event)
    }

    protected abstract fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent?
    protected open fun updateProgramCounter(cpu: CPU): PcAction = NoChange

}
data class InstructionResult(val pcAction: PcAction, val event: EmulatorEvent?)
