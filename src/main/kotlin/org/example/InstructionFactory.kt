package org.example

object InstructionFactory {
    fun createInstruction(instructionWord: UShort): Instruction {
        val instruction = instructionWord.toInt()

        val opcode = extractOpcode(instruction)
        val rX = extractRegX(instruction)
        val rY = extractRegY(instruction)
        val rZ = extractRegZ(instruction)
        val aaa = extractAddress(instruction)

        return when (opcode) {
            0x0 -> {
                val bb = extractLowByte(instruction)
                StoreInstruction(rX, bb)
            }
            0x1 -> AddInstruction(rX, rY, rZ)
            0x2 -> SubInstruction(rX, rY, rZ)
            0x3 -> ReadInstruction(rX)
            0x4 -> WriteInstruction(rX)
            0x5 -> JumpInstruction(aaa)
            0x6 -> ReadKeyboardInstruction(rX)
            0x7 -> SwitchMemoryInstruction()
            0x8 -> SkipIfEqualInstruction(rX, rY)
            0x9 -> SkipIfNotEqualInstruction(rX, rY)
            0xA -> SetAInstruction(aaa)
            0xB -> {
                val bb = extractMiddleByte(instruction)
                SetTInstruction(bb)
            }
            0xC -> ReadTInstruction(rX)
            0xD -> ConvertToBase10Instruction(rX)
            0xE -> ConvertByteToAsciiInstruction(rX, rY)
            0xF -> DrawInstruction(rX, rY, rZ)
            else -> throw IllegalArgumentException("Unknown opcode: $opcode")
        }
    }

    private fun extractOpcode(instruction: Int) = (instruction shr 12) and 0xF
    private fun extractRegX(instruction: Int) = (instruction shr 8) and 0xF
    private fun extractRegY(instruction: Int) = (instruction shr 4) and 0xF
    private fun extractRegZ(instruction: Int) = instruction and 0xF
    private fun extractAddress(instruction: Int) = (instruction and 0xFFF).toUShort()
    private fun extractLowByte(instruction: Int) = (instruction and 0xFF).toUByte()
    private fun extractMiddleByte(instruction: Int) = ((instruction shr 4) and 0xFF).toUByte()
}