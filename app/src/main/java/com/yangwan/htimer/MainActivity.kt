package com.yangwan.htimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yangwan.htimer.ui.theme.HTimerTheme
import com.yangwan.htimer.ui.timer.TimerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HTimerTheme {
                TimerScreen()
            }
        }
    }
}