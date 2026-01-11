@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.yangwan.htimer.ui.timer

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yangwan.htimer.domain.timer.TimerStatus

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val isRunning = state.status == TimerStatus.RUNNING
    val scrambleText by viewModel.scramble

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    val buttonScale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = 0.45f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "buttonScale"
    )

    val sinkY by animateFloatAsState(
        targetValue = if (pressed) 12f else 0f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessLow
        ),
        label = "sinkY"
    )

    val morphProgress by animateFloatAsState(
        targetValue = if (isRunning) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "timer_morph"
    )

    val animatedBgColor by animateColorAsState(
        targetValue = when (state.status) {
            TimerStatus.READY -> colorScheme.primaryContainer.copy(alpha = 0.9f)
            TimerStatus.HOLDING -> colorScheme.errorContainer
            else -> colorScheme.surface
        },
        animationSpec = tween(500),
        label = "bg_color"
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        viewModel.handlePress()
                        if (tryAwaitRelease()) viewModel.handleRelease()
                    }
                )
            },
        color = animatedBgColor
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val cellWidth = maxWidth * 0.11f
            val baseFontSize = (cellWidth.value * 1.6f).sp
            val time = state.timeMillis
            val minsStr = "%02d".format(time / 60000)
            val secsInMin = (time % 60000) / 1000
            val millisStr = "%03d".format(time % 1000)
            val timerTextColor = if (isRunning) colorScheme.primary else colorScheme.onSurface

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { clip = false },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints.copy(minWidth = 0))
                            val layoutWidth = (placeable.width * (1f - morphProgress)).toInt()
                            layout(layoutWidth, placeable.height) {
                                placeable.placeRelative(layoutWidth - placeable.width, 0)
                            }
                        }
                        .graphicsLayer {
                            alpha = 1f - morphProgress
                            scaleX = 1f - morphProgress
                            scaleY = 1f - morphProgress
                            transformOrigin = TransformOrigin(1f, 1f)
                        }
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        StaticCell(cellWidth, minsStr[0].toString(), baseFontSize, timerTextColor)
                        StaticCell(cellWidth, minsStr[1].toString(), baseFontSize, timerTextColor)
                        StaticCell(cellWidth, ":", baseFontSize, timerTextColor)
                        StaticCell(cellWidth, (secsInMin / 10).toString(), baseFontSize, timerTextColor)
                    }
                }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.graphicsLayer {
                        val s = 1f + 0.35f * morphProgress
                        scaleX = s
                        scaleY = s
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                ) {
                    val secondsToShow = if (isRunning) (time / 1000).toString() else (secsInMin % 10).toString()
                    Text(
                        text = secondsToShow,
                        style = TextStyle.Default.copy(
                            color = timerTextColor,
                            fontSize = baseFontSize,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        modifier = Modifier
                            .height(cellWidth * 1.6f)
                            .wrapContentWidth(unbounded = true),
                        softWrap = false
                    )
                    StaticCell(cellWidth, ".", baseFontSize, timerTextColor)
                    StaticCell(cellWidth, millisStr[0].toString(), baseFontSize, timerTextColor)
                }

                Box(
                    modifier = Modifier
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints.copy(minWidth = 0))
                            val layoutWidth = (placeable.width * (1f - morphProgress)).toInt()
                            layout(layoutWidth, placeable.height) {
                                placeable.placeRelative(0, 0)
                            }
                        }
                        .graphicsLayer {
                            alpha = 1f - morphProgress
                            scaleX = 1f - morphProgress
                            scaleY = 1f - morphProgress
                            transformOrigin = TransformOrigin(0f, 1f)
                        }
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        StaticCell(cellWidth, millisStr[1].toString(), baseFontSize, timerTextColor)
                        StaticCell(cellWidth, millisStr[2].toString(), baseFontSize, timerTextColor)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp)
                    .graphicsLayer {
                        alpha = (1f - morphProgress).coerceIn(0f, 1f)
                        translationY = morphProgress * 300f
                        clip = false
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = scrambleText,
                    transitionSpec = {
                        val enter = slideInHorizontally(animationSpec = tween(400)) { it / 4 } +
                                scaleIn(initialScale = 0.85f, animationSpec = tween(400)) +
                                fadeIn(animationSpec = tween(400))

                        val exit = slideOutHorizontally(animationSpec = tween(350)) { -it / 2 } +
                                scaleOut(targetScale = 0.9f, animationSpec = tween(350)) +
                                fadeOut(animationSpec = tween(250))

                        (enter togetherWith exit).using(SizeTransform(clip = false))
                    },
                    modifier = Modifier
                        .padding(vertical = 80.dp)
                        .graphicsLayer { clip = false },
                    label = "scramble_card_swap"
                ) { targetScramble ->
                    val overlayColor = if (isDark) Color.White.copy(0.08f) else Color.Black.copy(0.04f)
                    val adaptiveCardColor = overlayColor.compositeOver(animatedBgColor)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .graphicsLayer {
                                scaleX = buttonScale
                                scaleY = buttonScale
                                translationY = sinkY
                                transformOrigin = TransformOrigin(0.5f, 0.5f)
                            }
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                enabled = !isRunning
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.refreshScramble()
                            },
                        shape = MaterialTheme.shapes.extraLarge,
                        color = adaptiveCardColor,
                        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.12f)),
                        tonalElevation = if (pressed) 1.dp else 4.dp
                    ) {
                        Text(
                            text = targetScramble,
                            modifier = Modifier.padding(horizontal = 28.dp, vertical = 22.dp),
                            style = TextStyle.Default.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                color = colorScheme.secondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StaticCell(
    width: androidx.compose.ui.unit.Dp,
    char: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(width * 1.6f),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = char,
            style = TextStyle.Default.copy(
                color = textColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            softWrap = false,
            modifier = Modifier.wrapContentWidth(unbounded = true)
        )
    }
}