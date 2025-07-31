package org.example

interface DisplayStrategy {
    fun render(frameBuffer: UByteArray)  // Full render
    fun renderPartial(frameBuffer: UByteArray, dirtyPositions: Set<Int>)
}