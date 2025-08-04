package org.example

sealed class EmulatorEvent

sealed class SystemEvent : EmulatorEvent() {
    data class Halt(val message: String) : SystemEvent()
    object SwitchMemory : SystemEvent()

}

sealed class DisplayEvent : EmulatorEvent() {
    object Refresh : DisplayEvent()
    data class DrawPixel(val x: Int, val y: Int, val value: UByte) : DisplayEvent()
}

sealed class TimerEvent : EmulatorEvent() {
    data class Tick(val value: UByte) : TimerEvent()
    data class Set(val value: UByte) : TimerEvent()
}
