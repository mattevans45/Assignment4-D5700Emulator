package org.example

import java.util.Scanner

fun main() {
    val emulator = EmulatorFacade()
    val scanner = Scanner(System.`in`)

    println("D5700 Emulator")
    print("Enter the path to the D5700 ROM file: ")
    val romPath = scanner.nextLine()

    try {
        emulator.loadProgram(romPath)
        emulator.testTimerTicks() // Optional: enable timer debug logs
        emulator.start()
    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    }
}
