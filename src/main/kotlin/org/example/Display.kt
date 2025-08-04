package org.example

class Display : EmulatorObserver {
    private val frameBuffer = UByteArray(64) // 8x8 display
    private var strategy: DisplayStrategy = ConsoleDisplayStrategy()

    fun setDisplayStrategy(strategy: DisplayStrategy) {
        this.strategy = strategy
    }

    private fun renderChanges() {
        strategy.render(frameBuffer)
    }

    override fun onEmulatorEvent(event: EmulatorEvent) {
            if (event !is DisplayEvent) return

            when (event) {
                is DisplayEvent.DrawPixel -> {
                    if (event.x in 0..7 && event.y in 0..7) {
                        val index = event.x * 8 + event.y
                        frameBuffer[index] = event.value
                        renderChanges()
                    }
                }
                is DisplayEvent.Refresh -> {
                    renderChanges()
                }
            }

    }
}