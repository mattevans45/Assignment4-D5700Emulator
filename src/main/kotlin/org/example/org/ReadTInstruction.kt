package org.example

import org.example.CPU
import org.example.EmulatorEvent
import org.example.EventDrivenDisplay
import org.example.Instruction
import org.example.Keyboard

data class ReadTInstruction(val rX: Int) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent? {
        cpu.setRegister(rX, cpu.getTimer())
        return null
    }
}