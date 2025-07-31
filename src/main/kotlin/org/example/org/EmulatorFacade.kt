package org.example

import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.abs

enum class EmulatorState {
    UNINITIALIZED,
    READY,
    RUNNING,
    PAUSED,
    HALTED
}

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
class EmulatorFacade {
    private val logQueue = LinkedBlockingQueue<String>()
    private val consoleThread = Thread {
        while (true) {
            try {
                val msg = logQueue.take()
                kotlin.io.println(msg)
            } catch (e: InterruptedException) {
                break
            }
        }
    }.apply {
        isDaemon = true
        priority = Thread.MIN_PRIORITY
    }

    private val cpu = CPU
    private val display = EventDrivenDisplay()
    private val ramStrategy = RamStrategy()
    private val romStrategy = RomStrategy()
    private val romFileReader = RomFileReader()
    private val romValidator = RomValidator()
    private val keyboard = Keyboard()

    private val timerObserver = TimerObserver(enableLogging = false)

    private var cpuExecutor: ScheduledExecutorService? = null
    private var timerExecutor: ScheduledExecutorService? = null
    private var timerFuture: ScheduledFuture<*>? = null


    private var cpuFuture: ScheduledFuture<*>? = null

    private var state: EmulatorState = EmulatorState.UNINITIALIZED
    @Volatile private var isRunning = false
    @Volatile private var isPaused = false


    private val CPU_FREQUENCY_HZ = 500
    private val TIMER_FREQUENCY_HZ = 60.0

    private val CPU_INTERVAL_MS = 1000L / CPU_FREQUENCY_HZ


    fun start() {
        if (!consoleThread.isAlive) consoleThread.start()
        if (state != EmulatorState.READY) {
            throw IllegalStateException("Emulator must be READY (after loadProgram) before starting.")
        }

        state = EmulatorState.RUNNING
        isRunning = true
        isPaused = false

        cpuExecutor = Executors.newSingleThreadScheduledExecutor { r ->
            Thread(r, "CPU-Thread").apply {
                priority = Thread.NORM_PRIORITY + 1
                isDaemon = false
            }
        }

        val cpuRunnable = Runnable {
            if (!isRunning || isPaused) return@Runnable
            try {
                val event = cpu.step(keyboard, display)
                if (event is Halt) {
                    logQueue.offer("Program halted: ${event.message}")
                    shutdown()
                }
            } catch (e: Exception) {
                logQueue.offer("CPU Execution Error: ${e.message}")
                shutdown()
            }
        }

        cpuFuture = cpuExecutor?.scheduleAtFixedRate(
            cpuRunnable,
            0,
            CPU_INTERVAL_MS,
            TimeUnit.MILLISECONDS
        )

        timerExecutor = Executors.newSingleThreadScheduledExecutor()

        val timerRunnable = Runnable {
            if (isRunning && state == EmulatorState.RUNNING) {
                cpu.decrementTimer()
            }
        }
        timerFuture = timerExecutor?.scheduleAtFixedRate(
            timerRunnable,
            0,
            (1000L / TIMER_FREQUENCY_HZ).toLong(),
            TimeUnit.MILLISECONDS
        )
    }

    fun loadProgram(path: String) {
        val data = romFileReader.readFromFile(path)
        if (!romValidator.validate(data)) {
            throw IllegalArgumentException("Invalid ROM file.")
        }
        romStrategy.load(data)
        cpu.init(romStrategy = romStrategy, ramStrategy = ramStrategy)
        cpu.reset()
        cpu.setTimer(0u)
        cpu.addObserver(timerObserver)

        state = EmulatorState.READY
        display.fullRefresh()
    }


    fun shutdown() {
        if (!isRunning) return

        println("Shutting down emulator...")
        isRunning = false
        state = EmulatorState.HALTED

        cpu.removeObserver(timerObserver)
        cpuFuture?.cancel(true)


        timerFuture?.cancel(true)
        timerExecutor?.shutdown()

        shutdownExecutor(cpuExecutor)

        println("emulator stopped.")
    }

    private fun shutdownExecutor(executor: ScheduledExecutorService?) {
        executor?.let { exec ->
            try {
                exec.shutdown()
                if (!exec.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    exec.shutdownNow()
                }
            } catch (e: InterruptedException) {
                exec.shutdownNow()
                Thread.currentThread().interrupt()
            }
        }
    }


    fun pause() {
        isPaused = true
        state = EmulatorState.PAUSED
        println("Emulator paused.")
    }

    fun resume() {
        isPaused = false
        state = EmulatorState.RUNNING
        println("Emulator resumed.")
    }

    fun isEmulatorRunning(): Boolean = isRunning
    fun getState(): EmulatorState = state
}