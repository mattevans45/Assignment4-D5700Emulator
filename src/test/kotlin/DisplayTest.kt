package org.example

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalUnsignedTypes::class)
class DisplayTest {

    private lateinit var display: Display
    private lateinit var testDisplayStrategy: TestDisplayStrategy

    @BeforeEach
    fun setUp() {
        display = Display()
        testDisplayStrategy = TestDisplayStrategy()
        display.setDisplayStrategy(testDisplayStrategy)
    }

    @Test
    fun `setDisplayStrategy should change the rendering strategy`() {
        val newStrategy = TestDisplayStrategy()

        display.setDisplayStrategy(newStrategy)

        // Trigger a refresh to verify new strategy is used
        display.onEmulatorEvent(DisplayEvent.Refresh)

        assertTrue(newStrategy.renderCalled)
        assertFalse(testDisplayStrategy.renderCalled)
    }

    @Test
    fun `onEmulatorEvent should ignore non-DisplayEvent events`() {
        display.onEmulatorEvent(SystemEvent.Halt("test"))
        display.onEmulatorEvent(TimerEvent.Tick(5u))
        display.onEmulatorEvent(SystemEvent.SwitchMemory)

        assertFalse(testDisplayStrategy.renderCalled)
        assertTrue(testDisplayStrategy.drawnPixels.isEmpty())
    }

    @Test
    fun `onEmulatorEvent should handle DrawPixel event within bounds`() {
        display.onEmulatorEvent(DisplayEvent.DrawPixel(3, 5, 0xAAu))

        // Check that pixel was drawn in frame buffer
        val expectedPixels = listOf(TestDisplayStrategy.DrawnPixel(3, 5, 0xAAu))
        assertEquals(expectedPixels, testDisplayStrategy.drawnPixels)
    }

    @Test
    fun `onEmulatorEvent should handle multiple DrawPixel events`() {
        display.onEmulatorEvent(DisplayEvent.DrawPixel(0, 0, 0x11u))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(7, 7, 0x22u))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(3, 4, 0x33u))

        val expectedPixels = listOf(
            TestDisplayStrategy.DrawnPixel(0, 0, 0x11u),
            TestDisplayStrategy.DrawnPixel(7, 7, 0x22u),
            TestDisplayStrategy.DrawnPixel(3, 4, 0x33u)
        )
        assertEquals(expectedPixels, testDisplayStrategy.drawnPixels)
    }

    @Test
    fun `onEmulatorEvent should ignore DrawPixel events outside bounds`() {
        display.onEmulatorEvent(DisplayEvent.DrawPixel(-1, 5, 0xAAu))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(8, 5, 0xBBu))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(5, -1, 0xCCu))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(5, 8, 0xDDu))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(10, 10, 0xEEu))

        assertTrue(testDisplayStrategy.drawnPixels.isEmpty())
    }

    @Test
    fun `onEmulatorEvent should handle boundary coordinates correctly`() {

        display.onEmulatorEvent(DisplayEvent.DrawPixel(0, 0, 0x11u))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(0, 7, 0x22u))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(7, 0, 0x33u))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(7, 7, 0x44u))

        val expectedPixels = listOf(
            TestDisplayStrategy.DrawnPixel(0, 0, 0x11u),
            TestDisplayStrategy.DrawnPixel(0, 7, 0x22u),
            TestDisplayStrategy.DrawnPixel(7, 0, 0x33u),
            TestDisplayStrategy.DrawnPixel(7, 7, 0x44u)
        )
        assertEquals(expectedPixels, testDisplayStrategy.drawnPixels)
    }

    @Test
    fun `onEmulatorEvent should handle Refresh event`() {
        display.onEmulatorEvent(DisplayEvent.Refresh)

        assertTrue(testDisplayStrategy.renderCalled)
    }

    @Test
    fun `onEmulatorEvent should update frame buffer correctly`() {
        display.onEmulatorEvent(DisplayEvent.DrawPixel(2, 3, 0xFFu))

        // Verify the frame buffer was updated
        // Index calculation: x * 8 + y = 2 * 8 + 3 = 19
        assertEquals(0xFFu.toUByte(), testDisplayStrategy.lastFrameBuffer[19])
    }

    @Test
    fun `frame buffer should handle overwrites correctly`() {
        display.onEmulatorEvent(DisplayEvent.DrawPixel(1, 1, 0xAAu))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(1, 1, 0xBBu))

        // Should contain the latest value
        // Index: 1 * 8 + 1 = 9
        assertEquals(0xBBu.toUByte(), testDisplayStrategy.lastFrameBuffer[9])

        // Should have recorded both draw operations
        val expectedPixels = listOf(
            TestDisplayStrategy.DrawnPixel(1, 1, 0xAAu),
            TestDisplayStrategy.DrawnPixel(1, 1, 0xBBu)
        )
        assertEquals(expectedPixels, testDisplayStrategy.drawnPixels)
    }

    @Test
    fun `should preserve frame buffer state between events`() {
        display.onEmulatorEvent(DisplayEvent.DrawPixel(0, 0, 0x11u))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(1, 1, 0x22u))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(2, 2, 0x33u))

        // All pixels should remain in frame buffer
        assertEquals(0x11u.toUByte(), testDisplayStrategy.lastFrameBuffer[0])   // 0*8+0
        assertEquals(0x22u.toUByte(), testDisplayStrategy.lastFrameBuffer[9])   // 1*8+1
        assertEquals(0x33u.toUByte(), testDisplayStrategy.lastFrameBuffer[18])  // 2*8+2
    }

    @Test
    fun `should handle complete display refresh sequence`() {
        // Draw some pixels
        display.onEmulatorEvent(DisplayEvent.DrawPixel(0, 0, 0xAAu))
        display.onEmulatorEvent(DisplayEvent.DrawPixel(7, 7, 0xBBu))

        // Refresh display
        display.onEmulatorEvent(DisplayEvent.Refresh)

        // Verify strategy received the complete frame buffer
        assertTrue(testDisplayStrategy.renderCalled)
        assertEquals(0xAAu.toUByte(), testDisplayStrategy.lastFrameBuffer[0])   // 0*8+0
        assertEquals(0xBBu.toUByte(), testDisplayStrategy.lastFrameBuffer[63])  // 7*8+7
    }
}

class TestDisplayStrategy : DisplayStrategy {
    var renderCalled = false

    var lastFrameBuffer = UByteArray(64)

    val drawnPixels = mutableListOf<DrawnPixel>()

    data class DrawnPixel(val row: Int, val col: Int, val value: UByte)

    override fun render(frameBuffer: UByteArray) {
        renderCalled = true
        lastFrameBuffer = frameBuffer.copyOf()

        // Append new drawn pixels that differ from existing
        frameBuffer.forEachIndexed { index, value ->
            if (value != 0u.toUByte()) {
                val row = index / 8
                val col = index % 8
                val newPixel = DrawnPixel(row, col, value)
                if (drawnPixels.none { it == newPixel }) {
                    drawnPixels.add(newPixel)
                }
            }
        }
    }

}



