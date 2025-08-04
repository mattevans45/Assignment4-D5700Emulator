package org.example

class RomValidator {
    fun validate(data: UByteArray): Boolean {
        return data.isNotEmpty() && data.size <= 4096 - 0x200
    }
}