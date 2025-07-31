package org.example

data class SubInstruction(val rX: Int, val rY: Int, val rZ: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: Display): EmulatorEvent? {
        val valX = cpu.getRegister(rX)
        val valY = cpu.getRegister(rY)
        cpu.setRegister(rZ, (valX - valY).toUByte())
        return null
    }
}
