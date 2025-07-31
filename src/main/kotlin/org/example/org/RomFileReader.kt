package org.example

class RomFileReader {
    fun readFromFile(filePath: String): UByteArray {
        val file = java.io.File(filePath)
        if (!file.exists()) {
            throw IllegalArgumentException("File not found: $filePath")
        }
        val bytes = file.readBytes()
        return UByteArray(bytes.size) { i -> bytes[i].toUByte() }
    }
}