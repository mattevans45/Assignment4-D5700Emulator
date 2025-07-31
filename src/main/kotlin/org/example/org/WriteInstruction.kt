package org.example

data class WriteInstruction(val rX: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: Display): EmulatorEvent? {
        val address = cpu.getAddressRegister()
        val value = cpu.getRegister(rX)
        cpu.writeToDataMemory(address, value)
        return null
    }
}