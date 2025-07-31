package org.example

data class ExecutionResult(
    val pcAction: PcAction,
    val event: EmulatorEvent?
)
