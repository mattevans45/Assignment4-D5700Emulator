package org.example

object CPU {

    private val registers = UByteArray(8)

    private var programCounter: UShort = 0x0u
    private var timer: UByte = 0u
    private var addressRegister: UShort = 0u
    private var memoryFlag: Boolean = false

    private lateinit var romStrategy: RomStrategy
    private lateinit var ramStrategy: RamStrategy
    private lateinit var currentDataMemory: IMemoryStrategy

    private val instructionFactory = InstructionFactory

    fun init(romStrategy: RomStrategy, ramStrategy: RamStrategy) {
        this.romStrategy = romStrategy
        this.ramStrategy = ramStrategy
        this.currentDataMemory = ramStrategy
    }

    fun executeInstruction(keyboard: Keyboard, display: Display): ExecutionResult {
        val highByte = romStrategy.readByte(programCounter)
        val lowByte = romStrategy.readByte((programCounter + 1u).toUShort())
        val instructionWord = ((highByte.toInt() shl 8) or lowByte.toInt()).toUShort()

        if (instructionWord == 0x0000u.toUShort()) {
            return ExecutionResult(NoChange, Halt)
        }

        val instruction = instructionFactory.createInstruction(instructionWord)
        return instruction.execute(this, keyboard, display)
    }

    var onTimerTick: ((UByte) -> Unit)? = null

    fun decrementTimer() {
        if (timer > 0u) {
            timer = (timer - 1u).toUByte()
            onTimerTick?.invoke(timer)  // ← Debug hook
        }
    }

    fun toggleDataMemory() {
        memoryFlag = !memoryFlag
        currentDataMemory = if (memoryFlag) romStrategy else ramStrategy
    }
    fun getMemoryFlag(): Boolean = memoryFlag

    fun readFromDataMemory(address: UShort): UByte = currentDataMemory.readByte(address)
    fun writeToDataMemory(address: UShort, value: UByte) = currentDataMemory.writeByte(address, value)

    fun reset() {
        registers.fill(0u)
        programCounter = 0x0u
        timer = 0u
        addressRegister = 0u
        if(::ramStrategy.isInitialized) {
            currentDataMemory = ramStrategy // Reset data memory to RAM
        }
    }

    // Getters and setters for registers
    fun getRegister(index: Int): UByte = registers[index]
    fun setRegister(index: Int, value: UByte) { registers[index] = value }
    fun getTimer(): UByte = timer
    fun setTimer(value: UByte) { timer = value }
    fun getAddressRegister(): UShort = addressRegister
    fun setAddressRegister(value: UShort) { addressRegister = value }
    fun getPC(): UShort = programCounter
    fun setPC(address: UShort) {
        if (address.toInt() % 2 != 0) throw IllegalArgumentException("Program Counter must be an even number. Attempted to set to $address")
        programCounter = address
    }
    fun incrementPC(amount: Int) {
        programCounter = (programCounter + amount.toUShort()).toUShort()
    }
}
