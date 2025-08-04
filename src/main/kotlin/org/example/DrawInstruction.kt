package org.example

class DrawInstruction(val rX: Int, val rY: Int, val rZ: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent? {
        val char = cpu.getRegister(rX)
        return DisplayEvent.DrawPixel(x = rY, y = rZ, value = char)
    }
}