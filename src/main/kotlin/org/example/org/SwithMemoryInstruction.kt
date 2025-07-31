package org.example


class SwitchMemoryInstruction : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: Display): ExecutionStatus {
        cpu.toggleDataMemory()
        return SwitchMemory  // ExecutionStatus, not EmulatorEvent
    }
}
