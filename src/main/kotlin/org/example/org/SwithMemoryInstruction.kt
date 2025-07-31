package org.example

class SwitchMemoryInstruction : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent = SwitchMemory
}
