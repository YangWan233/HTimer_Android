@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.yangwan.htimer.ui.timer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yangwan.htimer.domain.timer.CubeState
import com.yangwan.htimer.domain.timer.TimerStatus

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val scrambleText by viewModel.scramble
    val cubeState by viewModel.cubeState
    val isRunning = state.status == TimerStatus.RUNNING

    val colorScheme = MaterialTheme.colorScheme
    var showFullPreview by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val buttonScale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "buttonScale"
    )
    val sinkY by animateFloatAsState(
        targetValue = if (pressed) 8f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
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

    if (showFullPreview) {
        PreviewDialog(cubeState = cubeState, onDismiss = { showFullPreview = false })
    }

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
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidthDp = maxWidth
            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            val sizeFactor = if (isLandscape) 0.07f else 0.11f
            val cellWidth = screenWidthDp * sizeFactor
            val baseFontSize = (cellWidth.value * 1.6f).sp

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                TimerDigitsDisplay(
                    timeMillis = state.timeMillis,
                    isRunning = isRunning,
                    morphProgress = morphProgress,
                    cellWidth = cellWidth,
                    baseFontSize = baseFontSize,
                    timerTextColor = if (isRunning) colorScheme.primary else colorScheme.onSurface
                )
            }

            ScrambleCardSection(
                modifier = Modifier.fillMaxSize(),
                scrambleText = scrambleText,
                cubeState = cubeState,
                isRunning = isRunning,
                morphProgress = morphProgress,
                buttonScale = buttonScale,
                sinkY = sinkY,
                interactionSource = interactionSource,
                animatedBgColor = animatedBgColor,
                parentWidth = screenWidthDp,
                onRefresh = { viewModel.refreshScramble() },
                onShowPreview = { showFullPreview = true }
            )
        }
    }
}

@Composable
private fun ScrambleCardSection(
    modifier: Modifier,
    scrambleText: String,
    cubeState: CubeState,
    isRunning: Boolean,
    morphProgress: Float,
    buttonScale: Float,
    sinkY: Float,
    interactionSource: MutableInteractionSource,
    animatedBgColor: Color,
    parentWidth: Dp,
    onRefresh: () -> Unit,
    onShowPreview: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = modifier
            .padding(24.dp)
            .graphicsLayer {
                alpha = (1f - morphProgress).coerceIn(0f, 1f)
                translationY = morphProgress * 250f
            }
    ) {
        val scrambleAlign = if (isLandscape) Alignment.BottomStart else Alignment.BottomCenter
        val verticalScramblePadding = if (isLandscape) 0.dp else 120.dp

        AnimatedContent(
            targetState = scrambleText,
            modifier = Modifier
                .align(scrambleAlign)
                .padding(bottom = verticalScramblePadding)
                .then(if (isLandscape) Modifier.widthIn(max = parentWidth * 0.45f) else Modifier.fillMaxWidth(0.88f)),
            transitionSpec = {
                if (isLandscape) {
                    (scaleIn(initialScale = 0.92f, animationSpec = tween(250)) + fadeIn(tween(250)))
                        .togetherWith(scaleOut(targetScale = 0.92f, animationSpec = tween(200)) + fadeOut(tween(200)))
                } else {
                    (slideInHorizontally(tween(400)) { it / 4 } + scaleIn(initialScale = 0.85f, animationSpec = tween(400)) + fadeIn(tween(400)))
                        .togetherWith(slideOutHorizontally(tween(350)) { -it / 2 } + scaleOut(targetScale = 0.9f, animationSpec = tween(350)) + fadeOut(tween(250)))
                        .using(SizeTransform(clip = false))
                }
            },
            label = "scramble_anim"
        ) { targetScramble ->
            val adaptiveCardColor = (if (isDark) Color.White.copy(0.08f) else Color.Black.copy(0.04f))
                .compositeOver(animatedBgColor)

            Surface(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                        translationY = sinkY
                    }
                    .pointerInput(isRunning) {
                        if (!isRunning) {
                            detectTapGestures(
                                onPress = { offset ->
                                    val press = PressInteraction.Press(offset)
                                    interactionSource.emit(press)
                                    tryAwaitRelease()
                                    interactionSource.emit(PressInteraction.Release(press))
                                },
                                onTap = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); onRefresh() },
                                onLongPress = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Scramble", targetScramble))
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    Toast.makeText(context, "公式已复制", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                color = adaptiveCardColor,
                border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.08f))
            ) {
                Text(
                    text = targetScramble,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colorScheme.secondary,
                        textAlign = if (isLandscape) TextAlign.Start else TextAlign.Center,
                        lineHeight = 18.sp
                    ),
                    maxLines = if (isLandscape) 4 else 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        val previewAlign = if (isLandscape) Alignment.BottomEnd else Alignment.BottomCenter

        Box(
            modifier = Modifier
                .align(previewAlign)
                .padding(bottom = if (isLandscape) 0.dp else 10.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onShowPreview()
                }
        ) {
            AnimatedContent(targetState = cubeState, label = "cube_preview") { currentCube ->
                ScramblePreview(cubeState = currentCube, cellSize = if (isLandscape) 11.dp else 10.dp)
            }
        }
    }
}

@Composable
private fun TimerDigitsDisplay(
    timeMillis: Long,
    isRunning: Boolean,
    morphProgress: Float,
    cellWidth: Dp,
    baseFontSize: TextUnit,
    timerTextColor: Color
) {
    val minsStr = "%02d".format(timeMillis / 60000)
    val secsInMin = (timeMillis % 60000) / 1000
    val millisStr = "%03d".format(timeMillis % 1000)

    Row(
        modifier = Modifier.graphicsLayer { clip = false },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Box(modifier = Modifier
            .layout { m, c ->
                val p = m.measure(c.copy(minWidth = 0))
                val lw = (p.width * (1f - morphProgress)).toInt()
                layout(lw, p.height) { p.placeRelative(lw - p.width, 0) }
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
                scaleX = s; scaleY = s; transformOrigin = TransformOrigin(0.5f, 1f)
            }
        ) {
            val secondsToShow = if (isRunning) (timeMillis / 1000).toString() else (secsInMin % 10).toString()
            Text(
                text = secondsToShow,
                style = TextStyle.Default.copy(
                    color = timerTextColor,
                    fontSize = baseFontSize,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                modifier = Modifier.height(cellWidth * 1.6f).wrapContentWidth(unbounded = true),
                softWrap = false
            )
            StaticCell(cellWidth, ".", baseFontSize, timerTextColor)
            StaticCell(cellWidth, millisStr[0].toString(), baseFontSize, timerTextColor)
        }

        Box(modifier = Modifier
            .layout { m, c ->
                val p = m.measure(c.copy(minWidth = 0))
                val lw = (p.width * (1f - morphProgress)).toInt()
                layout(lw, p.height) { p.placeRelative(0, 0) }
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
}

@Composable
private fun PreviewDialog(cubeState: CubeState, onDismiss: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "打乱预览图",
                    style = MaterialTheme.typography.labelLarge,
                    color = colorScheme.outline,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                ScramblePreview(cubeState = cubeState, cellSize = 18.dp)
                Text(
                    text = "点击关闭",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.outline.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        }
    }
}

@Composable
fun StaticCell(width: Dp, char: String, fontSize: TextUnit, textColor: Color) {
    Box(modifier = Modifier.width(width).height(width * 1.6f), contentAlignment = Alignment.BottomCenter) {
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