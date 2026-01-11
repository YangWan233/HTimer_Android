package com.yangwan.htimer.domain.timer

enum class TimerStatus {
    IDLE,
    HOLDING,
    READY,
    RUNNING,
    FINISHED
}

data class TimerState(
    val timeMillis: Long = 0L,
    val status: TimerStatus = TimerStatus.IDLE,
    val scramble: String = "U R2 F B R B2 L U' D2"
)