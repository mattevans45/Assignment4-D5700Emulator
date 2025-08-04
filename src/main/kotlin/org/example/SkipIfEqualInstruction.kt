package org.example

data class SkipIfEqualInstruction(val rX: Int, val rY: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent? {
        return null
    }
    override fun updateProgramCounter(cpu: CPU): PcAction {
        return if (cpu.getRegister(rX) == cpu.getRegister(rY)) {
            Increment(2)
        } else {
            NoChange
        }
    }
}