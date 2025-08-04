package org.example

class RomStrategy : IMemoryStrategy {
    private val rom = UByteArray(4096) // 4KB ROM

    fun load(data: UByteArray) {
        if (data.size > rom.size) {
            throw IllegalArgumentException("ROM data size (${data.size}) exceeds ROM capacity (${rom.size})")
        }
        data.copyInto(destination = rom, destinationOffset = 0, startIndex = 0, endIndex = data.size)
    }

    override fun size(): Int = rom.size

    override fun readByte(address: UShort): UByte {
        val addr = address.toInt()
        if (addr < 0 || addr >= rom.size) {
            throw IndexOutOfBoundsException("ROM address 0x${address.toString(16).uppercase().padStart(4, '0')} out of bounds (0x0000-0x${(rom.size - 1).toString(16).uppercase().padStart(4, '0')})")
        }
        return rom[addr]
    }

    override fun writeByte(address: UShort, value: UByte) {
        // Per spec, this should be an error for most ROMs.
        // For future-proofing, we could have a "writable" flag on the strategy.
        // For now, we throw an error to match the base spec.
        throw IllegalStateException("Cannot write to ROM at address 0x${address.toString(16).uppercase().padStart(4, '0')}")
    }
}