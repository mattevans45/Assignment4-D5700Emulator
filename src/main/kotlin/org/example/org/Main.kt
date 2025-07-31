package org.example

import java.util.Scanner
import kotlin.system.exitProcess


fun main() {
    val emulator = EmulatorFacade()
    val scanner = Scanner(System.`in`)

    println("=== D5700 Emulator ===")
    print("Enter the path to the D5700 ROM file: ")
    val romPath = scanner.nextLine()

    try {
        emulator.loadProgram(romPath)
        println("ROM loaded successfully!")


        Runtime.getRuntime().addShutdownHook(Thread {
            println("\nShutting down emulator...")
            emulator.shutdown()
        })

        println("Starting emulator...")
        emulator.start()
        println("Emulator is running. Press Ctrl+C to exit.")

    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
        emulator.shutdown()
    }
}