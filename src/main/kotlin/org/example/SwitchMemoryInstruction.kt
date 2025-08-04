package org.example

class SwitchMemoryInstruction : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard): EmulatorEvent {
        return SystemEvent.SwitchMemory
    }
}