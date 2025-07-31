package org.example

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

enum class EmulatorState { UNINITIALIZED, READY, RUNNING, HALTED }

class EmulatorFacade {
    private val cpu = CPU
    private val display = Display()
    private val ramStrategy = RamStrategy()
    private val romStrategy = RomStrategy()
    private val romFileReader = RomFileReader()
    private val romValidator = RomValidator()
    private val keyboard = Keyboard()

    private var executor: ScheduledExecutorService? = null
    private var cpuFuture: ScheduledFuture<*>? = null
    private var timerFuture: ScheduledFuture<*>? = null

    private var state: EmulatorState = EmulatorState.UNINITIALIZED
    @Volatile private var isRunning = false

    // To reduce frequency of timer tick prints
    private var timerPrintCounter = 0
    private var lastTimerTickTime = System.nanoTime()

    fun testTimerTicks() {
        cpu.onTimerTick = { value ->
            timerPrintCounter++
            if (timerPrintCounter % 600 == 0) {  // print every 600 ticks (~every 10 seconds)
                val now = System.nanoTime()
                val elapsedMillis = (now - lastTimerTickTime) / 1_000_000.0
                println("Timer = $value | Δt = %.3f ms".format(elapsedMillis))
                lastTimerTickTime = now
            }
        }
    }


    fun start() {
        if (state != EmulatorState.READY) {
            throw IllegalStateException("Emulator must be READY (after loadProgram) before starting.")
        }

        state = EmulatorState.RUNNING
        isRunning = true
        executor = Executors.newScheduledThreadPool(2) // CPU + Timer

        // Setup display dirty callback to trigger refresh only when needed
        display.setOnDirtyCallback {
            // Schedule refresh on executor thread to avoid blocking CPU thread
            executor?.execute {
                display.refresh()
            }
        }

        // CPU execution at 500Hz (every 2ms)
        val cpuRunnable = Runnable {
            if (!isRunning) return@Runnable
            try {
                val result = cpu.executeInstruction(keyboard, display)

                // Handle PC Action
                when (val action = result.pcAction) {
                    is Increment -> cpu.incrementPC(action.amount)
                    is SetPc -> cpu.setPC(action.address)
                    is NoChange -> { /* Do nothing */ }
                }

                // Handle Emulator Events
                result.event?.let { event ->
                    when (event) {
                        SwitchMemory -> cpu.toggleDataMemory()
                        Halt -> {
                            println("Program halted.")
                            shutdown()
                        }
                        DisplayRefresh -> {
                            // Now handled automatically by dirty callback, so no-op here
                        }
                    }
                }
            } catch (e: Exception) {
                println("CPU Execution Error: ${e.message}")
                e.printStackTrace()
                shutdown()
            }
        }

        cpuFuture = executor?.scheduleAtFixedRate(cpuRunnable, 0, 2, TimeUnit.MILLISECONDS)

        // Timer Runnable at 60Hz (every ~16ms)
        val timerRunnable = Runnable {
            if (!isRunning) return@Runnable
            cpu.decrementTimer()
        }

        timerFuture = executor?.scheduleAtFixedRate(timerRunnable, 0, 16, TimeUnit.MILLISECONDS)


        println("Emulator started!")
    }

    fun loadProgram(path: String) {
        val data = romFileReader.readFromFile(path)
        if (!romValidator.validate(data)) {
            throw IllegalArgumentException("Invalid ROM file.")
        }

        romStrategy.load(data)
        cpu.init(romStrategy = romStrategy, ramStrategy = ramStrategy)
        cpu.reset()
        display.setDisplayStrategy(ConsoleDisplayStrategy())


        state = EmulatorState.READY
    }

    fun shutdown() {
        if (!isRunning) return

        isRunning = false
        state = EmulatorState.HALTED

        executor?.shutdown()
        try {
            if (executor?.awaitTermination(1, TimeUnit.SECONDS) == false) {
                executor?.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor?.shutdownNow()
        }

        println("Emulator stopped.")
    }

    fun restart() {
        if (state != EmulatorState.HALTED) {
            throw IllegalStateException("Can only restart from HALTED state.")
        }

        cpu.reset()

        state = EmulatorState.READY
        println("Emulator reset. Ready to start again.")
    }
}
