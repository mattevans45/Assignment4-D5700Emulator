package org.example

//data class StoreInstruction(val rX: Int, val bb: UByte) : Instruction() {
//    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent? {
//        cpu.setRegister(rX, bb)
//        return null
//    }
//}

data class StoreInstruction(val rX: Int, val bb: UByte) : Instruction() {
    override fun performOperation(cpu: CPU, keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent? {
        println("[DEBUG] STORE: Loading $bb (${bb.toInt()}) into r$rX")
        cpu.setRegister(rX, bb)
        return null
    }
}