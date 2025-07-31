package org.example


sealed class PcAction
data class Increment(val amount: Int) : PcAction()
data class SetPc(val address: UShort) : PcAction()
object NoChange : PcAction()