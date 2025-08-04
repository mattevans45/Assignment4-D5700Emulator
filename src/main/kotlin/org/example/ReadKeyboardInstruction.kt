package org.example

data class ReadKeyboardInstruction(val rX: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent? {
        val value = keyboard.readHexInput()
        cpu.setRegister(rX, value)
        return null
    }
}