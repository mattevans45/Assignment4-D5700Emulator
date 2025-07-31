package org.example

class SimpleConsoleDisplayStrategy : DisplayStrategy {
    private val rows = 8
    private val cols = 8
    private val blankChar = '.'

    override fun render(frameBuffer: UByteArray) {
        printDisplay(frameBuffer, emptySet())
    }

    override fun renderPartial(frameBuffer: UByteArray, dirtyPositions: Set<Int>) {
        printDisplay(frameBuffer, dirtyPositions)
    }

    private fun printDisplay(frameBuffer: UByteArray, dirtyPositions: Set<Int>) {
        println("\n" + "=".repeat(50))
        println("                8x8 DISPLAY")
        println("=".repeat(50))

        // Column headers
        print("    ")
        for (col in 0 until cols) {
            print(" $col ")
        }
        println("     HEX VALUES")

        for (row in 0 until rows) {
            print(" $row  ")

            // Character display
            for (col in 0 until cols) {
                val index = row * cols + col
                val byteValue = frameBuffer[index]
                val displayChar = when {
                    byteValue.toInt() == 0 -> blankChar
                    byteValue <= 127u -> {
                        val char = byteValue.toInt().toChar()
                        if (char.isISOControl()) blankChar else char
                    }
                    else -> '?'
                }

                if (index in dirtyPositions) {
                    print("[$displayChar]")
                } else {
                    print(" $displayChar ")
                }
            }

            print("   ")

            // Hex values
            for (col in 0 until cols) {
                val index = row * cols + col
                val hex = String.format("%02X", frameBuffer[index].toInt())
                if (index in dirtyPositions) {
                    print("[$hex]")
                } else {
                    print(" $hex ")
                }
            }

            println()
        }

        if (dirtyPositions.isNotEmpty()) {
            val updates = dirtyPositions.map { "(${ it / 8 },${ it % 8 })" }
            println("Updated: ${updates.joinToString(", ")}")
        }

        println("=".repeat(50))
    }
}
