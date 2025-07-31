package org.example

class Keyboard {
    private val scanner = java.util.Scanner(System.`in`)

    fun readHexInput(): UByte {
        print("INPUT REQUIRED: ")
        val line = scanner.nextLine()?.trim() ?: ""
        if (line.isEmpty()) return 0u
        return line.toUByte(16)
    }
}