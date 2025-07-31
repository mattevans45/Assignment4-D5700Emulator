package org.example

data class SetTInstruction(val bb: UByte) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: Display): EmulatorEvent? {
        cpu.setTimer(bb)
        return null
    }
}