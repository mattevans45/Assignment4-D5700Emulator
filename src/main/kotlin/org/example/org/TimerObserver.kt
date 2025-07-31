
package org.example


class TimerObserver(
    private val enableLogging: Boolean = false,
    private val display: EventDrivenDisplay? = null
) : EmulatorObserver {

    companion object {
        private const val TIMER_HZ = 60L
    }

    private var verboseLogging = false
    private var lastTimerValue: UByte = 0u
    private var startTime: Long = 0L
    private var loggingEnabled = enableLogging
    private var expectedTicks: UByte = 60u

    override fun onEmulatorEvent(event: EmulatorEvent) {
        when (event) {
            is TimerTick -> {
                val currentTime = System.currentTimeMillis()

                if (event.value > 0u && (event.value > lastTimerValue || startTime == 0L)) {
                    startTime = currentTime
                    expectedTicks = event.value
                }

                if (loggingEnabled) {
                    if (verboseLogging) {
                        println("[TIMER] Tick: ${event.value}")
                    }
                    if (event.value == 0u.toUByte() && startTime > 0L) {
                        logTimerCompletion(currentTime)
                    }
                }

                display?.let {
                    val value = event.value.toInt()
                    if (value in 0..99) {
                        val char1 = '0' + value / 10
                        val char2 = '0' + value % 10
                        it.drawCharacter(0, 0, char1.code.toUByte())
                        it.drawCharacter(0, 1, char2.code.toUByte())
                    } else {
                        it.drawCharacter(0, 0, '?'.code.toUByte())
                        it.drawCharacter(0, 1, '?'.code.toUByte())
                    }

                }

                lastTimerValue = event.value
            }
            else -> {
                // ignore
            }
        }
    }

    private fun logTimerCompletion(currentTime: Long) {
        val elapsed = currentTime - startTime
        val expected = (expectedTicks.toInt() * 1000L) / TIMER_HZ
        val percentError = ((elapsed - expected).toDouble() / expected) * 100
        println("[TIMER] Completed $expectedTicks->0 in ${elapsed}ms (expected ${expected}ms, error: ${"%.2f".format(percentError)}%)")
        startTime = 0L
    }

    fun setLoggingEnabled(enabled: Boolean) {
        loggingEnabled = enabled
    }

    fun setVerboseLogging(enabled: Boolean) {
        verboseLogging = enabled
    }
}