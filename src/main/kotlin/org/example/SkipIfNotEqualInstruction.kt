package org.example

data class SkipIfNotEqualInstruction(val rX: Int, val rY: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent? = null
    override fun updateProgramCounter(cpu: CPU): PcAction {
        return if (cpu.getRegister(rX) != cpu.getRegister(rY)) Increment(4) else Increment(2)
    }
}