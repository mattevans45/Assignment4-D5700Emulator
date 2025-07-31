package org.example

data class DrawInstruction(val rX: Int, val rY: Int, val rZ: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent? {
        val char = cpu.getRegister(rX)
        if (char > 127u) throw IllegalStateException("Cannot draw non-ASCII character value: $char")
        display.drawCharacter(rY, rZ, char)
        return DisplayRefresh
    }
}