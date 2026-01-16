@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.yangwan.htimer.ui.timer

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yangwan.htimer.domain.timer.TimerStatus
import com.yangwan.htimer.ui.timer.components.*
import kotlinx.coroutines.launch

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    var currentRoute by remember { mutableStateOf("timer") }
    val context = LocalContext.current
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    BackHandler(enabled = true) {
        if (currentRoute != "timer") {
            currentRoute = "timer"
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < 2000) {
                (context as? Activity)?.finish()
            } else {
                lastBackPressTime = currentTime
                Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val fastOutSlowIn = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val animDuration = 350

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        AnimatedContent(
            targetState = currentRoute,
            transitionSpec = {
                val enterSpec = tween<IntOffset>(durationMillis = animDuration, easing = fastOutSlowIn)
                val fadeSpec = tween<Float>(durationMillis = animDuration)

                if (targetState == "about") {
                    (slideInHorizontally(animationSpec = enterSpec) { it } + fadeIn(fadeSpec))
                        .togetherWith(
                            slideOutHorizontally(animationSpec = enterSpec) { -it / 4 } + fadeOut(fadeSpec)
                        )
                } else {
                    (slideInHorizontally(animationSpec = enterSpec) { -it / 4 } + fadeIn(fadeSpec))
                        .togetherWith(
                            slideOutHorizontally(animationSpec = enterSpec) { it } + fadeOut(fadeSpec)
                        )
                }
            },
            label = "pro_navigation"
        ) { route ->
            Box(Modifier.fillMaxSize().graphicsLayer { clip = true }) {
                when (route) {
                    "timer" -> TimerMainContent(
                        viewModel = viewModel,
                        onNavigateToAbout = { currentRoute = "about" }
                    )
                    "about" -> AboutScreen(onBack = { currentRoute = "timer" })
                }
            }
        }
    }
}

@Composable
private fun TimerMainContent(viewModel: TimerViewModel, onNavigateToAbout: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val isRunning = state.status == TimerStatus.RUNNING
    val scrambleText by viewModel.scramble
    val cubeState by viewModel.cubeState

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isLandscape = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    var showFullPreview by remember { mutableStateOf(false) }

    val animatedBgColor by animateColorAsState(
        targetValue = when (state.status) {
            TimerStatus.READY -> MaterialTheme.colorScheme.primaryContainer
            TimerStatus.HOLDING -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(400),
        label = "bg_anim"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isRunning && !showFullPreview,
        drawerContent = {
            TimerDrawerSheet(onNavigate = { route ->
                if (route == "about") {
                    onNavigateToAbout()
                    scope.launch { drawerState.snapTo(DrawerValue.Closed) }
                } else {
                    scope.launch { drawerState.close() }
                }
            })
        }
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = animatedBgColor) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isRunning, drawerState.isOpen, showFullPreview) {
                        detectTapGestures(
                            onPress = {
                                if (!drawerState.isOpen && !showFullPreview) {
                                    viewModel.handlePress()
                                    if (tryAwaitRelease()) viewModel.handleRelease()
                                }
                            }
                        )
                    }
            ) {
                TimerTopBar(
                    visible = !isRunning,
                    cubeName = "三阶魔方",
                    currentBgColor = animatedBgColor,
                    onOpenSettings = { scope.launch { drawerState.open() } },
                    onSwitchCube = {}
                )

                TimeDisplay(
                    timeMillis = state.timeMillis,
                    isRunning = isRunning,
                    isLandscape = isLandscape
                )

                ScrambleSection(
                    scrambleText = scrambleText,
                    cubeState = cubeState,
                    isRunning = isRunning,
                    currentBgColor = animatedBgColor,
                    onRefresh = { viewModel.refreshScramble() },
                    onShowFullPreview = { showFullPreview = true }
                )

                if (showFullPreview) {
                    PreviewDialog(
                        cubeState = cubeState,
                        onDismiss = { showFullPreview = false }
                    )
                }
            }
        }
    }
}