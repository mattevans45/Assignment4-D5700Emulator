package org.example

object InstructionFactory {
    fun createInstruction(instructionWord: UShort): Instruction {
        val opcode = (instructionWord.toInt() shr 12) and 0xF

        // Extract common operands
        val rX = (instructionWord.toInt() shr 8) and 0xF
        val rY = (instructionWord.toInt() shr 4) and 0xF
        val rZ = instructionWord.toInt() and 0xF
        val bb = (instructionWord.toInt() and 0xFF).toUByte()
        val aaa = (instructionWord.toInt() and 0xFFF).toUShort()

        return when (opcode) {
            0x0 -> StoreInstruction(rX, bb)
            0x1 -> AddInstruction(rX, rY, rZ)
            0x2 -> SubInstruction(rX, rY, rZ)
            0x3 -> ReadInstruction(rX)
            0x4 -> WriteInstruction(rX)
            0x5 -> JumpInstruction(aaa)
            0x6 -> ReadKeyboardInstruction(rX)
            0x7 -> SwitchMemoryInstruction()
            0x8 -> SkipEqualInstruction(rX, rY)
            0x9 -> SkipNotEqualInstruction(rX, rY)
            0xA -> SetAInstruction(aaa)
            0xB -> SetTInstruction(bb)
            0xC -> ReadTInstruction(rX)
            0xD -> ConvertToBase10Instruction(rX)
            0xE -> ConvertByteToAsciiInstruction(rX, rY)
            0xF -> DrawInstruction(rX, rY, rZ)
            else -> throw IllegalArgumentException("Unknown opcode: $opcode")
        }
    }
}