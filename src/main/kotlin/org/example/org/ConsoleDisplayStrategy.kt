package org.example



class ConsoleDisplayStrategy : DisplayStrategy {
    private val rows = 8
    private val cols = 8
    private val blankChar = '.'

    override fun render(frameBuffer: UByteArray) {
        println("\n--- SCREEN ---")
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val char = frameBuffer[row * cols + col].toInt().toChar()
                print("$char ")
            }
            println()
        }
        println("--------------")
    }

    override fun renderPartial(frameBuffer: UByteArray, dirtyPositions: Set<Int>) {
        // Collect rows that have any dirty position
        val rowsToPrint = dirtyPositions.map { it / 8 }.toSet()

        for (row in rowsToPrint.sorted()) {
            val line = (0..7).joinToString(" ") {
                val char = frameBuffer[row * 8 + it].toInt().toChar()
                (if (char.isISOControl()) '.' else char).toString()
            }
            println(line)
        }
        println("--------------")
    }

}
