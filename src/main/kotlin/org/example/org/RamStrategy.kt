package org.example

//class RamStrategy : IMemoryStrategy {
//    private val ram = UByteArray(4096) // 4KB RAM
//
//    override fun readByte(address: UShort): UByte {
//        return if (address.toInt() < ram.size) ram[address.toInt()] else 0u
//    }
//
//    override fun writeByte(address: UShort, value: UByte) {
//        if (address.toInt() < ram.size) {
//            ram[address.toInt()] = value
//        }
//    }
//}



class RamStrategy : IMemoryStrategy {
    private val memory = UByteArray(4096) // 4KB RAM

    override fun readByte(address: UShort): UByte {
        return memory[address.toInt()]
    }

    override fun writeByte(address: UShort, value: UByte) {
        memory[address.toInt()] = value
    }
}