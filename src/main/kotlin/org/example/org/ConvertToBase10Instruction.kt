package org.example

data class ConvertToBase10Instruction(val rX: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent? {
        val value = cpu.getRegister(rX).toInt()
        val hundreds = (value / 100).toUByte()
        val tens = ((value % 100) / 10).toUByte()
        val ones = (value % 10).toUByte()

        val baseAddress = cpu.getAddressRegister()
        cpu.writeToDataMemory(baseAddress, hundreds)
        cpu.writeToDataMemory((baseAddress + 1u).toUShort(), tens)
        cpu.writeToDataMemory((baseAddress + 2u).toUShort(), ones)
        return null
    }
}
