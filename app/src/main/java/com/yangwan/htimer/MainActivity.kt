package com.yangwan.htimer

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yangwan.htimer.ui.theme.HTimerTheme
import com.yangwan.htimer.ui.timer.TimerScreen
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        setContent {
            HTimerTheme {
                TimerScreen()
            }
        }
    }
}