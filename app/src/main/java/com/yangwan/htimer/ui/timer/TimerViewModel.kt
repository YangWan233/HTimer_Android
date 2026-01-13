package com.yangwan.htimer.ui.timer

import android.os.SystemClock
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yangwan.htimer.domain.timer.CubeState
import com.yangwan.htimer.domain.timer.TimerState
import com.yangwan.htimer.domain.timer.TimerStatus
import com.yangwan.htimer.util.Scrambler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimerViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimerState())
    val state = _state.asStateFlow()

    var scramble = mutableStateOf(Scrambler.next())
        private set

    private val _cubeState = mutableStateOf(CubeState().apply { applyScramble(scramble.value) })
    val cubeState: State<CubeState> = _cubeState

    private var timerJob: Job? = null
    private var prepareJob: Job? = null

    fun handlePress() {
        when (_state.value.status) {
            TimerStatus.RUNNING -> stopTimer()
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
            TimerStatus.READY -> startTimer()
            TimerStatus.HOLDING -> _state.value = _state.value.copy(status = TimerStatus.IDLE)
            else -> {}
        }
    }

    private fun startTimer() {
        val startTime = SystemClock.elapsedRealtime()
        _state.value = _state.value.copy(status = TimerStatus.RUNNING, timeMillis = 0L)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
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
        updateScramble()
    }

    fun refreshScramble() {
        if (_state.value.status != TimerStatus.RUNNING) {
            updateScramble()
        }
    }

    private fun updateScramble() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.Default) {
                val nextScramble = Scrambler.next()
                val newCube = CubeState()
                newCube.applyScramble(nextScramble)
                nextScramble to newCube
            }
            scramble.value = result.first
            _cubeState.value = result.second
        }
    }
}