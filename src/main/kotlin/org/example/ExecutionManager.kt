package org.example

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class ExecutionManager {
    private var cpuExecutor: ScheduledExecutorService? = null
    private var timerExecutor: ScheduledExecutorService? = null
    private var cpuFuture: ScheduledFuture<*>? = null
    private var timerFuture: ScheduledFuture<*>? = null

    private val CPU_FREQUENCY_HZ = 500
    private val TIMER_FREQUENCY_HZ = 60.0
    private val CPU_INTERVAL_MS = 1000L / CPU_FREQUENCY_HZ

    fun start(
        cpuRunnable: Runnable,
        timerRunnable: Runnable
    ) {
        cpuExecutor = Executors.newSingleThreadScheduledExecutor { r ->
            Thread(r, "CPU-Thread").apply {
                priority = Thread.NORM_PRIORITY + 1
                isDaemon = false
            }
        }

        cpuFuture = cpuExecutor?.scheduleAtFixedRate(
            cpuRunnable, 0, CPU_INTERVAL_MS, TimeUnit.MILLISECONDS
        )

        timerExecutor = Executors.newSingleThreadScheduledExecutor { r ->
            Thread(r, "Timer-Thread").apply {
                isDaemon = true
            }
        }

        timerFuture = timerExecutor?.scheduleAtFixedRate(
            timerRunnable, 0, (1000L / TIMER_FREQUENCY_HZ).toLong(), TimeUnit.MILLISECONDS
        )
    }

    fun shutdown() {
        cpuFuture?.cancel(true)
        timerFuture?.cancel(true)

        listOf(cpuExecutor, timerExecutor).forEach { executor ->
            executor?.let { shutdownExecutor(it) }
        }

        cpuExecutor = null
        timerExecutor = null
        cpuFuture = null
        timerFuture = null
    }

    private fun shutdownExecutor(executor: ScheduledExecutorService) {
        try {
            executor.shutdown()
            if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}