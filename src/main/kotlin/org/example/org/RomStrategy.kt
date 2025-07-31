package org.example

class RomStrategy : IMemoryStrategy {
    private val rom = UByteArray(4096) // 4KB ROM

    fun load(data: UByteArray) {
        println("[DEBUG] Loading ${data.size} bytes into ROM.")
        // Load data starting at address 0x0
        data.copyInto(destination = rom, destinationOffset = 0, startIndex = 0, endIndex = data.size)
    }

    override fun readByte(address: UShort): UByte {
        val value = if (address.toInt() < rom.size) rom[address.toInt()] else 0u
        // Disabling verbose debug logging for cleaner output
        // println("[DEBUG] Reading from ROM at 0x${address.toString(16).uppercase()}: Value 0x${value.toString(16).uppercase()}")
        return value
    }

    override fun writeByte(address: UShort, value: UByte) {
        // Per spec, this should be an error for most ROMs.
        // For future-proofing, we could have a "writable" flag on the strategy.
        // For now, we throw an error to match the base spec.
        throw IllegalStateException("Attempted to write to read-only memory (ROM) at address $address")
    }
}