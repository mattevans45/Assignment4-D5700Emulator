package org.example



import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

@OptIn(ExperimentalUnsignedTypes::class)
class RomStrategyTest {

    private lateinit var romStrategy: RomStrategy

    @BeforeEach
    fun setUp() {
        romStrategy = RomStrategy()
    }

    @Test
    fun `load should store ROM data correctly`() {
        val testData = ubyteArrayOf(0x12u, 0x34u, 0x56u, 0x78u)

        romStrategy.load(testData)

        assertEquals(0x12u.toUByte(), romStrategy.readByte(0u))
        assertEquals(0x34u.toUByte(), romStrategy.readByte(1u))
        assertEquals(0x56u.toUByte(), romStrategy.readByte(2u))
        assertEquals(0x78u.toUByte(), romStrategy.readByte(3u))
    }

    @Test
    fun `readByte should return correct values from loaded ROM`() {
        val testData = ubyteArrayOf(0xAAu, 0xBBu, 0xCCu, 0xDDu)
        romStrategy.load(testData)

        assertEquals(0xAAu.toUByte(), romStrategy.readByte(0u))
        assertEquals(0xBBu.toUByte(), romStrategy.readByte(1u))
        assertEquals(0xCCu.toUByte(), romStrategy.readByte(2u))
        assertEquals(0xDDu.toUByte(), romStrategy.readByte(3u))
    }

    @Test
    fun `readByte should return zero for addresses beyond ROM size`() {
        val testData = ubyteArrayOf(0xAAu, 0xBBu)
        romStrategy.load(testData)

        assertEquals(0x00u.toUByte(), romStrategy.readByte(100u))
        assertEquals(0x00u.toUByte(), romStrategy.readByte(1000u))
    }

    @Test
    fun `writeByte should throw exception`() {
        val testData = ubyteArrayOf(0x12u, 0x34u)
        romStrategy.load(testData)

        assertThrows<Exception> {
            romStrategy.writeByte(0u, 0xAAu)
        }
    }

    @Test
    fun `writeByte should throw exception even for invalid addresses`() {
        val testData = ubyteArrayOf(0x12u, 0x34u)
        romStrategy.load(testData)

        assertThrows<Exception> {
            romStrategy.writeByte(1000u, 0xAAu)
        }
    }

    @Test
    fun `load with empty array should work`() {
        val emptyData = ubyteArrayOf()

        romStrategy.load(emptyData)

        assertEquals(0x00u.toUByte(), romStrategy.readByte(0u))
    }

    @Test
    fun `load should replace previous data`() {
        val firstData = ubyteArrayOf(0x11u, 0x22u)
        val secondData = ubyteArrayOf(0xAAu, 0xBBu, 0xCCu)

        romStrategy.load(firstData)
        assertEquals(0x11u.toUByte(), romStrategy.readByte(0u))

        romStrategy.load(secondData)
        assertEquals(0xAAu.toUByte(), romStrategy.readByte(0u))
        assertEquals(0xBBu.toUByte(), romStrategy.readByte(1u))
        assertEquals(0xCCu.toUByte(), romStrategy.readByte(2u))
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
class RamStrategyTest {

    private lateinit var ramStrategy: RamStrategy

    @BeforeEach
    fun setUp() {
        ramStrategy = RamStrategy()
    }

    @Test
    fun `readByte should return zero initially`() {
        assertEquals(0x00u.toUByte(), ramStrategy.readByte(0u))
        assertEquals(0x00u.toUByte(), ramStrategy.readByte(100u))
    }

    @Test
    fun `writeByte and readByte should work correctly`() {
        ramStrategy.writeByte(0u, 0xAAu)
        ramStrategy.writeByte(100u, 0xBBu)

        assertEquals(0xAAu.toUByte(), ramStrategy.readByte(0u))
        assertEquals(0xBBu.toUByte(), ramStrategy.readByte(100u))
    }

    @Test
    fun `writeByte should overwrite previous values`() {
        ramStrategy.writeByte(50u, 0x11u)
        assertEquals(0x11u.toUByte(), ramStrategy.readByte(50u))

        ramStrategy.writeByte(50u, 0x22u)
        assertEquals(0x22u.toUByte(), ramStrategy.readByte(50u))
    }

    @Test
    fun `should handle full address range`() {
        // Test boundary addresses for 4K RAM
        ramStrategy.writeByte(0x0000u, 0xAAu)
        ramStrategy.writeByte(0x0FFFu, 0xBBu)  // max valid address in 4K RAM

        assertEquals(0xAAu.toUByte(), ramStrategy.readByte(0x0000u))
        assertEquals(0xBBu.toUByte(), ramStrategy.readByte(0x0FFFu))
    }


    @Test
    fun `should maintain independence between addresses`() {
        ramStrategy.writeByte(0u, 0xAAu)
        ramStrategy.writeByte(1u, 0xBBu)
        ramStrategy.writeByte(2u, 0xCCu)

        assertEquals(0xAAu.toUByte(), ramStrategy.readByte(0u))
        assertEquals(0xBBu.toUByte(), ramStrategy.readByte(1u))
        assertEquals(0xCCu.toUByte(), ramStrategy.readByte(2u))

        // Modifying one shouldn't affect others
        ramStrategy.writeByte(1u, 0xDDu)
        assertEquals(0xAAu.toUByte(), ramStrategy.readByte(0u))
        assertEquals(0xDDu.toUByte(), ramStrategy.readByte(1u))
        assertEquals(0xCCu.toUByte(), ramStrategy.readByte(2u))
    }

    @Test
    fun `should handle edge case values`() {
        ramStrategy.writeByte(0u, 0x00u)
        ramStrategy.writeByte(1u, 0xFFu)

        assertEquals(0x00u.toUByte(), ramStrategy.readByte(0u))
        assertEquals(0xFFu.toUByte(), ramStrategy.readByte(1u))
    }
}