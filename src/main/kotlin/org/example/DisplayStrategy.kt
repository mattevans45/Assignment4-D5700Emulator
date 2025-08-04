package org.example

interface DisplayStrategy {
    fun render(frameBuffer: UByteArray)
}