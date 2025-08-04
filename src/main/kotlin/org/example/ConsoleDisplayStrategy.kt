package org.example

@OptIn(kotlin.ExperimentalUnsignedTypes::class)
class ConsoleDisplayStrategy : DisplayStrategy {
    private val rows = 8
    private val cols = 8
    private val blankChar = '.'

    override fun render(frameBuffer: UByteArray) {
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