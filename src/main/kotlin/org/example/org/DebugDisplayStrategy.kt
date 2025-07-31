package org.example

class DebugDisplayStrategy : DisplayStrategy {
    override fun render(buffer: UByteArray) {
        println("--- DEBUG DISPLAY (HEX) ---")
        for (row in 0 until 8) {
            buffer.slice((row * 8) until (row * 8 + 8)).forEach { byte ->
                print("%02X ".format(byte.toInt()))
            }
            println()
        }
        println("---------------------------\n")
    }
    override fun renderPartial(frameBuffer: UByteArray, dirtyPositions: Set<Int>) {
        // Simple implementation: just call full render for now
        render(frameBuffer)
    }

}
