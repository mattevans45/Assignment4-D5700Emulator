package org.example

interface IMemoryStrategy {
    fun readByte(address: UShort): UByte
    fun writeByte(address: UShort, value: UByte)
    fun size(): Int
}

