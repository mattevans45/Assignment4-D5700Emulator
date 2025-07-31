package org.example

data class ReadInstruction(val rX: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: Display): EmulatorEvent? {
        val address = cpu.getAddressRegister()
        val value = cpu.readFromDataMemory(address)
        cpu.setRegister(rX, value)
        return null
    }
}
