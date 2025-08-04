package org.example

data class StoreInstruction(val rX: Int, val bb: UByte) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent? {
        cpu.setRegister(rX, bb)
        return null
    }
}