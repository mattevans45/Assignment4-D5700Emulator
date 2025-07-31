package org.example

object InstructionFactory {
    fun createInstruction(instructionWord: UShort): Instruction {
        val instruction = instructionWord.toInt()

        val opcode = (instruction shr 12) and 0xF
        val rX = (instruction shr 8) and 0xF
        val rY = (instruction shr 4) and 0xF
        val rZ = instruction and 0xF
        val aaa = (instruction and 0xFFF).toUShort()

        return when (opcode) {
            0x0 -> {
                val bb = (instruction and 0xFF).toUByte()
                StoreInstruction(rX, bb)
            }
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
            0xB -> {
                val bb = ((instruction shr 4) and 0xFF).toUByte()
                SetTInstruction(bb)
            }
            0xC -> ReadTInstruction(rX)
            0xD -> ConvertToBase10Instruction(rX)
            0xE -> ConvertByteToAsciiInstruction(rX, rY)
            0xF -> DrawInstruction(rX, rY, rZ)
            else -> throw IllegalArgumentException("Unknown opcode: $opcode")
        }
    }
}
