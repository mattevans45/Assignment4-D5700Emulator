package org.example

open class Keyboard {
    private val scanner = java.util.Scanner(System.`in`)

    open fun readHexInput(): UByte {
        print("INPUT REQUIRED: ")
        val line = scanner.nextLine()?.trim()?.take(2) ?: ""
        return try {
            line.toUByte(16)
        } catch (e: NumberFormatException) {
            println("Invalid hex input. Defaulting to 0.")
            0u
        }
    }
}
