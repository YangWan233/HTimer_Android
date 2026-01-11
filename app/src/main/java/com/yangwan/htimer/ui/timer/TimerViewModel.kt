package com.yangwan.htimer.ui.timer

import android.os.SystemClock
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yangwan.htimer.domain.timer.TimerState
import com.yangwan.htimer.domain.timer.TimerStatus
import com.yangwan.htimer.util.Scrambler
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimerState())
    val state = _state.asStateFlow()

    var scramble = mutableStateOf(Scrambler.next())
        private set

    private var timerJob: Job? = null
    private var prepareJob: Job? = null

    fun handlePress() {
        when (_state.value.status) {
            TimerStatus.RUNNING -> {
                stopTimer()
            }
            else -> {
                _state.value = _state.value.copy(status = TimerStatus.HOLDING)
                prepareJob?.cancel()
                prepareJob = viewModelScope.launch {
                    delay(300)
                    _state.value = _state.value.copy(status = TimerStatus.READY)
                }
            }
        }
    }

    fun handleRelease() {
        prepareJob?.cancel()
        when (_state.value.status) {
            TimerStatus.READY -> {
                startTimer()
            }
            TimerStatus.HOLDING -> {
                _state.value = _state.value.copy(status = TimerStatus.IDLE)
            }
            else -> {}
        }
    }

    private fun startTimer() {
        val startTime = SystemClock.elapsedRealtime()
        _state.value = _state.value.copy(
            status = TimerStatus.RUNNING,
            timeMillis = 0L
        )

        timerJob?.cancel()
        timerJob = viewModelScope.launch(Dispatchers.Main) {
            while (isActive) {
                _state.value = _state.value.copy(
                    timeMillis = SystemClock.elapsedRealtime() - startTime
                )
                delay(16)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _state.value = _state.value.copy(status = TimerStatus.FINISHED)
        scramble.value = Scrambler.next()
    }

    fun refreshScramble() {
        if (_state.value.status != TimerStatus.RUNNING) {
            scramble.value = Scrambler.next()
        }
    }
}