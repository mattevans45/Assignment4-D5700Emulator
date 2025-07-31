package org.example

import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

interface DisplayObserver {
    fun onFramebufferUpdate(indices: Set<Int>)
}

class EventDrivenDisplay {
    private val frameBuffer = UByteArray(64) // 8x8 display
    private var strategy: DisplayStrategy = ConsoleDisplayStrategy()
    private val observers = mutableSetOf<DisplayObserver>()

    private val dirtyPositions = mutableSetOf<Int>()

    fun setDisplayStrategy(strategy: DisplayStrategy) {
        this.strategy = strategy
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun drawCharacter(row: Int, col: Int, char: UByte) {
        if (row in 0..7 && col in 0..7) {
            val index = row * 8 + col
            if (frameBuffer[index] != char) {
                frameBuffer[index] = char
                dirtyPositions.add(index)

                renderChanges()
            }
        }
    }

    private fun renderChanges() {
        if (dirtyPositions.isNotEmpty()) {
            val updatedPositions = dirtyPositions.toSet()
            strategy.renderPartial(frameBuffer, updatedPositions)
            dirtyPositions.clear()
            notifyObservers(updatedPositions)
        }
    }

    fun refresh() {
        renderChanges()
    }

    fun fullRefresh() {
        strategy.render(frameBuffer)
        val allPositions = (0 until 64).toSet()
        dirtyPositions.clear()
        notifyObservers(allPositions)
    }


    fun clear() {
        frameBuffer.fill(0u)
        dirtyPositions.addAll(0 until 64)
        renderChanges()
    }

    private fun notifyObservers(indices: Set<Int>) {
        for (observer in observers) {
            observer.onFramebufferUpdate(indices)
        }
    }
}

