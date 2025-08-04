package org.example

data class ReadTInstruction(val rX: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent? {
        cpu.setRegister(rX, cpu.getTimer())
        return null
    }
}