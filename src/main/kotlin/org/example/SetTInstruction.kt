package org.example

class SetTInstruction(val bb: UByte) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent? {
        return TimerEvent.Set(bb)
    }
}