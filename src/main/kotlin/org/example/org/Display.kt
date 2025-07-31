package org.example

class Display {
    private val frameBuffer = UByteArray(64) // 8x8 display (64 cells)
    private val dirtyPositions = mutableSetOf<Int>() // track changed indices
    private var strategy: DisplayStrategy = ConsoleDisplayStrategy()

    fun setDisplayStrategy(strategy: DisplayStrategy) {
        this.strategy = strategy
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun drawCharacter(row: Int, col: Int, char: UByte) {
        if (row !in 0..7 || col !in 0..7) {
            println("[DEBUG] Draw call ignored: Coordinates ($row, $col) are out of bounds.")
            return
        }

        val index = row * 8 + col
        if (frameBuffer[index] != char) {
            frameBuffer[index] = char
            dirtyPositions.add(index)
            notifyDirty()
        }
    }

    /**
     * Refresh only the parts of the screen that have changed.
     */
    fun refresh() {
        if (dirtyPositions.isNotEmpty()) {
            strategy.renderPartial(frameBuffer, dirtyPositions)
            dirtyPositions.clear()
        }
    }

    // Callback invoked when display becomes dirty
    private var onDirtyCallback: (() -> Unit)? = null

    fun setOnDirtyCallback(callback: () -> Unit) {
        onDirtyCallback = callback
    }

    private fun notifyDirty() {
        onDirtyCallback?.invoke()
    }
}
