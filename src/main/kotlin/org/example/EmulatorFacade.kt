package org.example

import java.util.concurrent.LinkedBlockingQueue

class EmulatorFacade(
    private val romFileReader: RomFileReader = RomFileReader(),
    private val romValidator: RomValidator = RomValidator()
) {
    private val logQueue = LinkedBlockingQueue<String>()
    private val consoleThread = Thread {
        while (true) {
            try {
                val msg = logQueue.take()
                println(msg)
            } catch (e: InterruptedException) {
                break
            }
        }
    }.apply {
        isDaemon = true
        priority = Thread.MIN_PRIORITY
    }

    private val cpu = CPU
    private val display = Display()
    private val ramStrategy = RamStrategy()
    private val romStrategy = RomStrategy()
    private val keyboard = Keyboard()
    private val executionCoordinator = ExecutionManager()

    @Volatile
    private var state: EmulatorState = EmulatorState.UNINITIALIZED


    fun start() {
        if (!consoleThread.isAlive) consoleThread.start()

        if (!state.canTransitionTo(EmulatorState.RUNNING)) {
            throw IllegalStateException("Cannot start emulator from state: $state")
        }

        setState(EmulatorState.RUNNING)

        val cpuRunnable = Runnable {
            if (Thread.currentThread().isInterrupted) {
                return@Runnable
            }
            if (state != EmulatorState.RUNNING) return@Runnable
            try {
                val event = cpu.step(keyboard)
                if (event is SystemEvent.Halt) {
                    logQueue.offer("Program halted: ${event.message}")
                    shutdown()
                }
            } catch (e: Exception) {
                logQueue.offer("CPU Execution Error: ${e.message}")
                shutdown()
            }
        }

        val timerRunnable = Runnable {
            if (state == EmulatorState.RUNNING) {
                cpu.decrementTimer()
            }
        }

        executionCoordinator.start(cpuRunnable, timerRunnable)
    }


    fun loadProgram(path: String) {
        val data = romFileReader.readFromFile(path)
        if (!romValidator.validate(data)) {
            throw IllegalArgumentException("Invalid ROM file.")
        }
        romStrategy.load(data)
        cpu.init(romStrategy = romStrategy, ramStrategy = ramStrategy)

        cpu.addObserver(display)


        setState(EmulatorState.READY)
    }

    fun shutdown() {
        synchronized(this) {
            if (state == EmulatorState.HALTED) {
                return
            }
            println("Shutting down emulator...")
            setState(EmulatorState.HALTED)

            cpu.removeObserver(display)
            executionCoordinator.shutdown()

            println("Emulator stopped.")
        }
    }


    fun pause() {
        if (state.canTransitionTo(EmulatorState.PAUSED)) {
            setState(EmulatorState.PAUSED)
            println("Emulator paused.")
        }
    }

    fun resume() {
        if (state.canTransitionTo(EmulatorState.RUNNING)) {
            setState(EmulatorState.RUNNING)
            println("Emulator resumed.")
        }
    }

    private fun setState(newState: EmulatorState) {
        synchronized(this) {
            if (state.canTransitionTo(newState)) {
                state = newState
            } else {
                throw IllegalStateException("Cannot transition from $state to $newState")
            }
        }
    }

    fun getState(): EmulatorState = state
}