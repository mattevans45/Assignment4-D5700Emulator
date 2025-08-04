package org.example


import java.util.concurrent.CopyOnWriteArrayList

@OptIn(ExperimentalUnsignedTypes::class)
object CPU : EmulatorObserver {
    private val registers = UByteArray(8)
    private var programCounter: UShort = 0x0u
    private var timer: UByte = 0u
    private var addressRegister: UShort = 0u
    private var memoryFlag: Boolean = false

    private lateinit var romStrategy: RomStrategy
    private lateinit var ramStrategy: RamStrategy
    private lateinit var currentDataMemory: IMemoryStrategy
    private val instructionFactory = InstructionFactory

    fun isMemoryFlagSet(): Boolean = memoryFlag

    private val observers = CopyOnWriteArrayList<EmulatorObserver>()

    fun addObserver(observer: EmulatorObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: EmulatorObserver) {
        observers.remove(observer)
    }

    private fun notifyObservers(event: EmulatorEvent) {
        observers.forEach { it.onEmulatorEvent(event) }
    }

    override fun onEmulatorEvent(event: EmulatorEvent) {
        when (event) {
            is SystemEvent.SwitchMemory -> toggleDataMemory()
            is TimerEvent.Set -> setTimer(event.value)
            else -> {
            }
        }
    }

    fun init(romStrategy: RomStrategy, ramStrategy: RamStrategy) {
        this.romStrategy = romStrategy
        this.ramStrategy = ramStrategy
        this.currentDataMemory = ramStrategy
        addObserver(this)
    }

    fun step(keyboard: Keyboard): EmulatorEvent? {
        val highByte = romStrategy.readByte(programCounter)
        val lowByte = romStrategy.readByte((programCounter + 1u).toUShort())
        val instructionWord = ((highByte.toInt() shl 8) or lowByte.toInt()).toUShort()

        programCounter = (programCounter + 2u).toUShort()

        if (instructionWord == 0x0000u.toUShort()) {
            return SystemEvent.Halt("Encountered HALT instruction at PC=$programCounter")
        }

        val instruction = instructionFactory.createInstruction(instructionWord)
        val result = instruction.execute(this, keyboard)

        when (val action = result.pcAction) {
            is Increment -> programCounter = (programCounter + action.amount.toUShort()).toUShort()
            is SetPc -> programCounter = action.address
            is NoChange -> {
            }
        }

        val instructionEvent = result.event
        when (instructionEvent) {
            null -> {
            }

            is SystemEvent.Halt -> return instructionEvent
            else -> notifyObservers(instructionEvent)
        }

        if (instructionEvent is DisplayEvent.Refresh) {
            notifyObservers(DisplayEvent.Refresh)
        }

        return null
    }

    fun decrementTimer() {
        if (timer > 0u) {
            timer = (timer - 1u).toUByte()
            notifyObservers(TimerEvent.Tick(timer))
        }
    }


    private fun setTimer(value: UByte) {
        timer = value
        notifyObservers(TimerEvent.Tick(timer))
    }

    fun getTimer(): UByte = timer

    private fun toggleDataMemory() {
        memoryFlag = !memoryFlag
        currentDataMemory = if (memoryFlag) romStrategy else ramStrategy
    }

    fun getRegister(index: Int): UByte = registers[index]

    fun setRegister(index: Int, value: UByte) {
        registers[index] = value
    }

    fun getAddressRegister(): UShort = addressRegister
    fun setAddressRegister(value: UShort) {
        addressRegister = value
    }

    fun readFromDataMemory(address: UShort): UByte = currentDataMemory.readByte(address)
    fun writeToDataMemory(address: UShort, value: UByte) = currentDataMemory.writeByte(address, value)


    fun reset() {
        registers.fill(0u)
        programCounter = 0x0u
        timer = 0u
        addressRegister = 0u
        memoryFlag = false
        if (::ramStrategy.isInitialized) {
            currentDataMemory = ramStrategy
        }
        observers.clear()
        addObserver(this)
    }

}