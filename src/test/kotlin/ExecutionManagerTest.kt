package org.example

import org.junit.jupiter.api.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertTrue

class ExecutionManagerTest {

    private lateinit var manager: ExecutionManager

    @BeforeEach
    fun setup() {
        manager = ExecutionManager()
    }

    @Test
    fun `start schedules cpu and timer runnables`() {
        val cpuRuns = AtomicInteger(0)
        val timerRuns = AtomicInteger(0)

        val cpuRunnable = Runnable { cpuRuns.incrementAndGet() }
        val timerRunnable = Runnable { timerRuns.incrementAndGet() }

        manager.start(cpuRunnable, timerRunnable)

        // Give some time for scheduled tasks to run
        Thread.sleep(100) // 100ms to allow a few runs

        // Both should have run at least once
        assertTrue(cpuRuns.get() > 0, "CPU runnable should have run")
        assertTrue(timerRuns.get() > 0, "Timer runnable should have run")

        manager.shutdown()
    }

    @Test
    fun `shutdown cancels and shuts down executors`() {
        val cpuRunnable = Runnable { }
        val timerRunnable = Runnable { }

        manager.start(cpuRunnable, timerRunnable)
        manager.shutdown()

        // After shutdown, starting again should create new executors without error
        manager.start(cpuRunnable, timerRunnable)
        manager.shutdown()
    }
}
