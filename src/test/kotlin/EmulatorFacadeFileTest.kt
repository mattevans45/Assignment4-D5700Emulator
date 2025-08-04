package org.example

import org.example.EmulatorFacade
import org.example.EmulatorState
import org.junit.jupiter.api.*
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmulatorFacadeFileIntegrationTest {

    private lateinit var facade: EmulatorFacade

    @BeforeEach
    fun setUp() {
        facade = EmulatorFacade()
    }

    @Test
    fun `loadProgram should set state to READY for valid ROM file`() {
        // Arrange: create a valid temporary ROM file
        val tempFile = createTempFile(suffix = ".bin")
        tempFile.writeBytes(ByteArray(4096 - 0x200) { 0x01 })  // fill with valid data size < max

        try {
            // Act
            facade.loadProgram(tempFile.absolutePath)

            // Assert
            assertEquals(EmulatorState.READY, facade.getState())
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `loadProgram should throw IllegalArgumentException for invalid ROM file (empty)`() {
        // Arrange: create an empty temporary ROM file
        val tempFile = createTempFile(suffix = ".bin")
        tempFile.writeBytes(ByteArray(0))  // empty file

        try {
            // Act & Assert
            val exception = assertFailsWith<IllegalArgumentException> {
                facade.loadProgram(tempFile.absolutePath)
            }
            Assertions.assertEquals("Invalid ROM file.", exception.message)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `loadProgram should throw IllegalArgumentException when file does not exist`() {
        // Arrange: path to a nonexistent file
        val invalidPath = "this_file_does_not_exist.bin"

        // Act & Assert
        val exception = assertFailsWith<IllegalArgumentException> {
            facade.loadProgram(invalidPath)
        }
        Assertions.assertTrue(exception.message?.contains("File not found") == true)
    }
}

