package org.example

data class ConvertByteToAsciiInstruction(val rX: Int, val rY: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent? {
        val value = cpu.getRegister(rX).toInt()
        if (value > 0xF) throw IllegalStateException("Value for ASCII conversion must be 0-F. Got $value")
        // '0' is ASCII 48, 'A' is ASCII 65
        val asciiValue = if (value < 10) (value + 48) else (value - 10 + 65)
        cpu.setRegister(rY, asciiValue.toUByte())
        return null
    }
}