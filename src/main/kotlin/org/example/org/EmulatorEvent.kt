package org.example

sealed class EmulatorEvent
object SwitchMemory : EmulatorEvent()
data class Halt(val message: String) : EmulatorEvent()
object DisplayRefresh : EmulatorEvent()
data class TimerTick(val value: UByte) : EmulatorEvent()
data class FramebufferUpdate(val indices: Set<Int>) : EmulatorEvent()