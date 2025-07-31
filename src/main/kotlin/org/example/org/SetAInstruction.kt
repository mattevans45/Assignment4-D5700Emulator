package org.example

data class SetAInstruction(val aaa: UShort) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent? {
        cpu.setAddressRegister(aaa)
        return null
    }
}