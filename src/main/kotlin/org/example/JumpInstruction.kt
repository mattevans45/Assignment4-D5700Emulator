package org.example

data class JumpInstruction(val aaa: UShort) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent? {
        if (aaa.toInt() % 2 != 0) {
            throw IllegalStateException("JUMP address must be even. Got $aaa")
        }
        return null
    }
    override fun updateProgramCounter(cpu: CPU): PcAction = SetPc(aaa)
}
