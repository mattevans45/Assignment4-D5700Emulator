package org.example

class RamStrategy : IMemoryStrategy {
    private val ram = UByteArray(4096) // 4KB RAM

    override fun size(): Int = ram.size

    override fun readByte(address: UShort): UByte {
        val addr = address.toInt()
        if (addr < 0 || addr >= ram.size) {
            throw IndexOutOfBoundsException("RAM address 0x${address.toString(16).uppercase().padStart(4, '0')} out of bounds (0x0000-0x${(ram.size - 1).toString(16).uppercase().padStart(4, '0')})")
        }
        return ram[addr]
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    override fun writeByte(address: UShort, value: UByte) {
        val addr = address.toInt()
        if (addr < 0 || addr >= ram.size) {
            throw IndexOutOfBoundsException("RAM address 0x${address.toString(16).uppercase().padStart(4, '0')} out of bounds (0x0000-0x${(ram.size - 1).toString(16).uppercase().padStart(4, '0')})")
        }
        ram[addr] = value
    }

}