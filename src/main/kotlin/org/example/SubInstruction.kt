package org.example

data class SubInstruction(val rX: Int, val rY: Int, val rZ: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent? {
        val valX = cpu.getRegister(rX).toInt()
        val valY = cpu.getRegister(rY).toInt()
        val result = maxOf(0, valX - valY)
        cpu.setRegister(rZ, result.toUByte())
        return null
    }
}