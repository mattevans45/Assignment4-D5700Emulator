    /**
     * Steps the CPU: fetch, decode, execute, and handle PC and internal events.
     * Returns the event (e.g., Halt) for the facade to handle globally.
     */

//package org.example
//
//object CPU {
//
//    private val registers = UByteArray(8)
//
//    private var programCounter: UShort = 0x0u
//    private var timer: UByte = 0u
//    private var addressRegister: UShort = 0u
//    private var memoryFlag: Boolean = false
//
//    private lateinit var romStrategy: RomStrategy
//    private lateinit var ramStrategy: RamStrategy
//    private lateinit var currentDataMemory: IMemoryStrategy
//
//    private val instructionFactory = InstructionFactory
//
//    // === Observer Support ===
//    private val observers = mutableListOf<EmulatorObserver>()
//
//    fun addObserver(observer: EmulatorObserver) {
//        observers.add(observer)
//    }
//
//    fun removeObserver(observer: EmulatorObserver) {
//        observers.remove(observer)
//    }
//
//    private fun notifyObservers(event: EmulatorEvent) {
//        observers.forEach { it.onEmulatorEvent(event) }
//    }
//
//    // === Initialization ===
//    fun init(romStrategy: RomStrategy, ramStrategy: RamStrategy) {
//        this.romStrategy = romStrategy
//        this.ramStrategy = ramStrategy
//        this.currentDataMemory = ramStrategy
//    }
//
//    // === Main Instruction Execution ===
//    fun executeInstruction(keyboard: Keyboard, display: Display): ExecutionResult {
//        val highByte = romStrategy.readByte(programCounter)
//        val lowByte = romStrategy.readByte((programCounter + 1u).toUShort())
//        val instructionWord = ((highByte.toInt() shl 8) or lowByte.toInt()).toUShort()
//
//        if (instructionWord == 0x0000u.toUShort()) {
//            return ExecutionResult(
//                NoChange,
//                event = Halt("Encountered HALT instruction at PC=$programCounter")
//            )
//        }
//
//        val instruction = instructionFactory.createInstruction(instructionWord)
//        return instruction.execute(this, keyboard, display)
//    }
//
//    // === Timer Tick Handling ===
//    fun decrementTimer() {
//        if (timer > 0u) {
//            timer = (timer - 1u).toUByte()
//            notifyObservers(TimerTick(timer.toUByte()))
//        }
//    }
//
//    fun toggleDataMemory() {
//        memoryFlag = !memoryFlag
//        currentDataMemory = if (memoryFlag) romStrategy else ramStrategy
//    }
//
//    fun notifyFramebufferChanged(indices: Set<Int>) {
//        notifyObservers(FramebufferUpdate(indices))
//    }
//
//    // === Memory Access ===
//    fun getMemoryFlag(): Boolean = memoryFlag
//    fun readFromDataMemory(address: UShort): UByte = currentDataMemory.readByte(address)
//    fun writeToDataMemory(address: UShort, value: UByte) = currentDataMemory.writeByte(address, value)
//
//    // === Reset State ===
//    fun reset() {
//        registers.fill(0u)
//        programCounter = 0x0u
//        timer = 0u
//        addressRegister = 0u
//        if (::ramStrategy.isInitialized) {
//            currentDataMemory = ramStrategy
//        }
//    }
//
//    // === Accessors ===
//    fun getRegister(index: Int): UByte = registers[index]
//    fun setRegister(index: Int, value: UByte) {
//        registers[index] = value
//    }
//
//    fun getTimer(): UByte = timer
//    fun setTimer(value: UByte) {
//        timer = value
//        notifyObservers(TimerTick(timer.toUByte()))
//    }
//
//    fun getAddressRegister(): UShort = addressRegister
//    fun setAddressRegister(value: UShort) {
//        addressRegister = value
//    }
//
//    fun getPC(): UShort = programCounter
//    fun setPC(address: UShort) {
//        if (address.toInt() % 2 != 0) throw IllegalArgumentException("Program Counter must be an even number. Attempted to set to $address")
//        programCounter = address
//    }
//
//    fun incrementPC(amount: Int) {
//        programCounter = (programCounter + amount.toUShort()).toUShort()
//    }
//}

package org.example

//object CPU {
//
//    private val registers = UByteArray(8)
//
//    private var programCounter: UShort = 0x0u
//    private var timer: UByte = 0u
//    private var addressRegister: UShort = 0u
//    private var memoryFlag: Boolean = false
//
//    private lateinit var romStrategy: RomStrategy
//    private lateinit var ramStrategy: RamStrategy
//    private lateinit var currentDataMemory: IMemoryStrategy
//
//    private val instructionFactory = InstructionFactory
//
//    private val observers = mutableListOf<EmulatorObserver>()
//
//    fun addObserver(observer: EmulatorObserver) {
//        observers.add(observer)
//    }
//
//    fun removeObserver(observer: EmulatorObserver) {
//        observers.remove(observer)
//    }
//
//    private fun notifyObservers(event: EmulatorEvent) {
//        observers.forEach { it.onEmulatorEvent(event) }
//    }
//
//    fun init(romStrategy: RomStrategy, ramStrategy: RamStrategy) {
//        this.romStrategy = romStrategy
//        this.ramStrategy = ramStrategy
//        this.currentDataMemory = ramStrategy
//    }
//
//    fun executeInstruction(keyboard: Keyboard, display: EventDrivenDisplay): ExecutionResult {
//        val highByte = romStrategy.readByte(programCounter)
//        val lowByte = romStrategy.readByte((programCounter + 1u).toUShort())
//        val instructionWord = ((highByte.toInt() shl 8) or lowByte.toInt()).toUShort()
//
//        if (instructionWord == 0x0000u.toUShort()) {
//            return ExecutionResult(
//                NoChange,
//                event = Halt("Encountered HALT instruction at PC=$programCounter")
//            )
//        }
//
//        val instruction = instructionFactory.createInstruction(instructionWord)
//        return instruction.execute(this, keyboard, display)
//    }
//
//
//    fun decrementTimer() {
//        if (timer > 0u) {
//            timer = (timer - 1u).toUByte()
//            notifyObservers(TimerTick(timer))
//        }
//    }
//
//    fun toggleDataMemory() {
//        memoryFlag = !memoryFlag
//        currentDataMemory = if (memoryFlag) romStrategy else ramStrategy
//    }
//
//    fun notifyFramebufferChanged(indices: Set<Int>) {
//        notifyObservers(FramebufferUpdate(indices))
//    }
//
//    fun getMemoryFlag(): Boolean = memoryFlag
//    fun readFromDataMemory(address: UShort): UByte = currentDataMemory.readByte(address)
//    fun writeToDataMemory(address: UShort, value: UByte) = currentDataMemory.writeByte(address, value)
//
//    fun reset() {
//        registers.fill(0u)
//        programCounter = 0x0u
//        timer = 0u
//        addressRegister = 0u
//        if (::ramStrategy.isInitialized) {
//            currentDataMemory = ramStrategy
//        }
//    }
//
//    // === Accessors ===
//    fun getRegister(index: Int): UByte = registers[index]
//    fun setRegister(index: Int, value: UByte) {
//        registers[index] = value
//    }
//
//    fun getTimer(): UByte = timer
//    fun setTimer(value: UByte) {
//        timer = value
//        notifyObservers(TimerTick(timer.toUByte()))
//    }
//
//    fun getAddressRegister(): UShort = addressRegister
//    fun setAddressRegister(value: UShort) {
//        addressRegister = value
//    }
//
//    fun getPC(): UShort = programCounter
//    fun setPC(address: UShort) {
//        if (address.toInt() % 2 != 0) throw IllegalArgumentException("Program Counter must be an even number. Attempted to set to $address")
//        programCounter = address
//    }
//
//    fun incrementPC(amount: Int) {
//        programCounter = (programCounter + amount.toUShort()).toUShort()
//    }
//}

//package org.example

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

    private val observers = mutableListOf<EmulatorObserver>()

    fun addObserver(observer: EmulatorObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: EmulatorObserver) {
        observers.remove(observer)
    }

    private fun notifyObservers(event: EmulatorEvent) {
        observers.forEach { it.onEmulatorEvent(event) }
    }

    fun init(romStrategy: RomStrategy, ramStrategy: RamStrategy) {
        this.romStrategy = romStrategy
        this.ramStrategy = ramStrategy
        this.currentDataMemory = ramStrategy
    }

    fun step(keyboard: Keyboard, display: EventDrivenDisplay): EmulatorEvent? {
        val highByte = romStrategy.readByte(programCounter)
        val lowByte = romStrategy.readByte((programCounter + 1u).toUShort())
        val instructionWord = ((highByte.toInt() shl 8) or lowByte.toInt()).toUShort()

        val currentPC = programCounter
        programCounter = (programCounter + 2u).toUShort()


        if (instructionWord == 0x0000u.toUShort()) {
            return Halt("Encountered HALT instruction at PC=$currentPC")
        }
        val instruction = instructionFactory.createInstruction(instructionWord)
        val result = instruction.execute(this, keyboard, display)

        when (val action = result.pcAction) {
            is Increment -> {
                programCounter = (programCounter + action.amount.toUShort()).toUShort()
            }
            is SetPc -> {
                programCounter = action.address
            }
            is NoChange -> {    
            }
        }

        when (val event = result.event) {
            SwitchMemory -> toggleDataMemory()
            is DisplayRefresh -> {
                notifyObservers(event)
            }
            is TimerTick -> {
                setTimer(event.value)
            }
            is FramebufferUpdate -> {
                notifyFramebufferChanged(event.indices)
            }
            is Halt -> return event 
            null -> {}
        }

        return null 
    }

    fun decrementTimer() {
        if (timer > 0u) {
            timer = (timer - 1u).toUByte()
            notifyObservers(TimerTick(timer))
        }
    }

    fun toggleDataMemory() {
        memoryFlag = !memoryFlag
        currentDataMemory = if (memoryFlag) romStrategy else ramStrategy
    }

    fun notifyFramebufferChanged(indices: Set<Int>) {
        notifyObservers(FramebufferUpdate(indices))
    }

    fun getMemoryFlag(): Boolean = memoryFlag
    fun readFromDataMemory(address: UShort): UByte = currentDataMemory.readByte(address)
    fun writeToDataMemory(address: UShort, value: UByte) = currentDataMemory.writeByte(address, value)

    fun reset() {
        registers.fill(0u)
        programCounter = 0x0u
        timer = 0u  // ← This should reset timer to 0
        addressRegister = 0u
        if (::ramStrategy.isInitialized) {
            currentDataMemory = ramStrategy
        }
    }

    fun getRegister(index: Int): UByte = registers[index]
    fun setRegister(index: Int, value: UByte) {
        registers[index] = value
    }

    fun getTimer(): UByte = timer
    fun setTimer(value: UByte) {
        val stackTrace = Thread.currentThread().stackTrace
        println("[CPU_DEBUG] Setting timer to ${value.toInt()} from: ${stackTrace[2].className}.${stackTrace[2].methodName}")
        timer = value
        notifyObservers(TimerTick(timer.toUByte()))
    }

    fun getAddressRegister(): UShort = addressRegister
    fun setAddressRegister(value: UShort) {
        addressRegister = value
    }

    fun getPC(): UShort = programCounter
    fun setPC(address: UShort) {
        if (address.toInt() % 2 != 0) throw IllegalArgumentException("Program Counter must be an even number. Attempted to set to $address")
        programCounter = address
    }

}