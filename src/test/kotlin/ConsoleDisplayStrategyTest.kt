package org.example

import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertTrue
import kotlin.test.assertContains

@OptIn(ExperimentalUnsignedTypes::class)
class ConsoleDisplayStrategyTest {

    @Test
    fun `render prints clear screen and display borders`() {
        val strategy = ConsoleDisplayStrategy()
        val buffer = UByteArray(64) { 0u } // all blank pixels

        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        try {
            strategy.render(buffer)
        } finally {
            System.setOut(originalOut)
        }

        val output = outputStream.toString()

        // Check clear screen escape code
        assertContains(output, "\u001B[2J\u001B[H")

        // Check top and bottom border line length (should be 8 cols * 3 chars + 2 borders = 26 chars)
        assertTrue(output.contains("+------------------------+"), "Output should contain border line")

        // Check at least one row with | and spaces
        assertTrue(output.contains("|"), "Output should contain row borders")

        // Blank pixels print as '.'
        assertTrue(output.contains("."), "Blank pixels should be rendered as '.'")
    }

    @Test
    fun `render prints ASCII characters for valid range`() {
        val strategy = ConsoleDisplayStrategy()
        val buffer = UByteArray(64) { 0u }

        // Put digits 0-9 in first 10 pixels
        for (i in 0..9) {
            buffer[i] = (i + 48).toUByte()  // ASCII '0'..'9'
        }
        // Put letters A-F in next 6 pixels
        for (i in 10..15) {
            buffer[i] = (i - 10 + 65).toUByte()  // ASCII 'A'..'F'
        }

        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        try {
            strategy.render(buffer)
        } finally {
            System.setOut(originalOut)
        }

        val output = outputStream.toString()

        // Check digits and letters appear (spaces in between, so we check for any digit or letter presence)
        for (c in '0'..'9') {
            assertTrue(output.contains(c), "Output should contain digit $c")
        }
        for (c in 'A'..'F') {
            assertTrue(output.contains(c), "Output should contain letter $c")
        }
    }

    @Test
    fun `render prints question mark for values over 127`() {
        val strategy = ConsoleDisplayStrategy()
        val buffer = UByteArray(64) { 0u }

        // Put values >127 to test '?'
        buffer[0] = 128u
        buffer[1] = 200u
        buffer[2] = 255u

        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        try {
            strategy.render(buffer)
        } finally {
            System.setOut(originalOut)
        }

        val output = outputStream.toString()

        // Should print '?' for those positions
        val questionMarkCount = output.count { it == '?' }
        assertTrue(questionMarkCount >= 3, "Output should contain at least 3 question marks")
    }

    @Test
    fun `render prints dot for control and whitespace characters`() {
        val strategy = ConsoleDisplayStrategy()
        val buffer = UByteArray(64) { 0u }

        // Control characters (0x00 - 0x1F), plus whitespace (space = 0x20, tab=0x09)
        buffer[0] = 0u   // null char -> '.'
        buffer[1] = 9u   // tab -> '.'
        buffer[2] = 10u  // newline -> '.'
        buffer[3] = 13u  // carriage return -> '.'
        buffer[4] = 32u  // space -> '.'

        val outputStream = ByteArrayOutputStream()
        val originalOut = System.out
        System.setOut(PrintStream(outputStream))

        try {
            strategy.render(buffer)
        } finally {
            System.setOut(originalOut)
        }

        val output = outputStream.toString()

        // All these positions should print '.' (blankChar)
        val dotCount = output.count { it == '.' }
        assertTrue(dotCount >= 5, "Output should contain at least 5 dots for control and whitespace characters")
    }
}

