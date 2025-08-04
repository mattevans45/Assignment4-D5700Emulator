package org.example

data class ConvertByteToAsciiInstruction(val rX: Int, val rY: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent? {
        val value = cpu.getRegister(rX).toInt()
        if (value > 0xF) throw IllegalStateException("Value for ASCII conversion must be 0-F. Got $value")

        val asciiValue = if (value < 10) (value + 48) else (value - 10 + 65)
        cpu.setRegister(rY, asciiValue.toUByte())
        return null
    }
}