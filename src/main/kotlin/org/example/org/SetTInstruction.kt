package org.example

class SetTInstruction(val bb: UByte) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent? {
        cpu.setTimer(bb)
        return TimerTick(bb)
    }
}
