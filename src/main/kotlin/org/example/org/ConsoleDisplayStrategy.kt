package org.example



@OptIn(kotlin.ExperimentalUnsignedTypes::class)
// Enhanced Console Display Strategy that always shows the full 8x8 grid
class ConsoleDisplayStrategy : DisplayStrategy {
    private val rows = 8
    private val cols = 8
    private val blankChar = '.'

    override fun render(frameBuffer: UByteArray) {
        printFullDisplay(frameBuffer)
    }

    override fun renderPartial(frameBuffer: UByteArray, dirtyPositions: Set<Int>) {
        // Always show the full display for partial updates
        printFullDisplay(frameBuffer)
    }

    private fun printFullDisplay(frameBuffer: UByteArray) {
        print("\u001B[2J\u001B[H")
        println("+" + "-".repeat(cols * 3) + "+")
        for (row in 0 until rows) {
            print("|")
            for (col in 0 until cols) {
                val index = row * cols + col
                print(" ${toDisplayChar(frameBuffer[index])} ")
            }
            println("|")
        }
        println("+" + "-".repeat(cols * 3) + "+\n")
    }

    private fun toDisplayChar(byteValue: UByte): Char =
        when {
            byteValue.toInt() == 0 -> blankChar
            byteValue <= 127u -> {
                val char = byteValue.toInt().toChar()
                if (char.isISOControl() || char.isWhitespace()) blankChar else char
            }
            else -> '?'
        }
}

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
class OptimizedConsoleDisplayStrategy : DisplayStrategy {
    private val rows = 8
    private val cols = 8
    private val blankChar = '.'
    private var updateCount = 0
    private val updateThreshold = 10

    override fun render(frameBuffer: UByteArray) {
        printDisplay(frameBuffer)
    }

    override fun renderPartial(frameBuffer: UByteArray, dirtyPositions: Set<Int>) {
        updateCount++
        if (updateCount >= updateThreshold) {
            render(frameBuffer)
            updateCount = 0
        }
    }

    private fun printDisplay(frameBuffer: UByteArray) {
        print("\u001B[2J\u001B[H")
        println("8x8 Display:")
        println("-".repeat(24))
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val index = row * cols + col
                print(" ${toDisplayChar(frameBuffer[index])} ")
            }
            println()
        }
        println("-".repeat(24) + "\n")
    }

    private fun toDisplayChar(byteValue: UByte): Char =
        when {
            byteValue.toInt() == 0 -> blankChar
            byteValue <= 127u -> {
                val char = byteValue.toInt().toChar()
                if (char.isISOControl() || char.isWhitespace()) blankChar else char
            }
            else -> '?'
        }
}