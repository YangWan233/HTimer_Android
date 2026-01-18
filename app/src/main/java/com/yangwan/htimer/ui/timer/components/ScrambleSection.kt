package com.yangwan.htimer.ui.timer.components

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yangwan.htimer.domain.timer.CubeState
import com.yangwan.htimer.ui.timer.ScramblePreview

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ScrambleSection(
    scrambleText: String,
    cubeState: CubeState,
    isRunning: Boolean,
    currentBgColor: Color = MaterialTheme.colorScheme.surface,
    onRefresh: () -> Unit,
    onShowFullPreview: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val colorScheme = MaterialTheme.colorScheme
    val configuration = LocalConfiguration.current

    var showCrossDialog by remember { mutableStateOf(false) }
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp

    val adaptiveFontSize = remember(screenWidth) { (screenWidth / 25f).coerceIn(14f, 19f).sp }
    val adaptiveCellSize = remember(screenWidth, isLandscape) {
        if (isLandscape) (screenWidth / 55f).coerceIn(9f, 13f).dp
        else (screenWidth * 0.35f / 12f).coerceIn(8f, 12.5f).dp
    }
    val containerWidthFraction = if (isLandscape) 0.45f else 0.92f

    var wasRunning by remember { mutableStateOf(false) }
    LaunchedEffect(isRunning) {
        if (!isRunning) kotlinx.coroutines.delay(30)
        wasRunning = isRunning
    }

    val morphProgress by animateFloatAsState(
        targetValue = if (isRunning) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scramble_morph"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    val adaptiveContainerColor = remember(currentBgColor, isPressed) {
        if (currentBgColor == colorScheme.surface) {
            if (isPressed) colorScheme.surfaceContainerHigh else colorScheme.surfaceContainerLow
        } else {
            val overlay = if (isPressed) 0.12f else 0.06f
            colorScheme.onSurface.copy(alpha = overlay).compositeOver(currentBgColor)
        }
    }
    val adaptiveContentColor = when (currentBgColor) {
        colorScheme.primaryContainer -> colorScheme.onPrimaryContainer
        colorScheme.errorContainer -> colorScheme.onErrorContainer
        else -> colorScheme.onSurfaceVariant
    }

    if (showCrossDialog) {
        CrossSolutionDialog(
            scramble = scrambleText,
            onDismiss = { showCrossDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .graphicsLayer {
                alpha = (1f - morphProgress).coerceIn(0f, 1f)
                translationY = morphProgress * 250f
                clip = false
            }
    ) {
        val scrambleAlign = if (isLandscape) Alignment.BottomStart else Alignment.BottomCenter

        if (!isRunning) {
            FilledTonalIconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showCrossDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 8.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = colorScheme.secondaryContainer.copy(alpha = 0.8f)
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lightbulb,
                    contentDescription = "Cross Solutions",
                    modifier = Modifier.size(22.dp),
                    tint = colorScheme.onSecondaryContainer
                )
            }
        }

        Box(
            modifier = Modifier
                .align(scrambleAlign)
                .padding(bottom = if (isLandscape) 0.dp else 140.dp)
                .fillMaxWidth(containerWidthFraction)
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                    transformOrigin = TransformOrigin.Center
                }
                .pointerInput(isRunning, scrambleText) {
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
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("Scramble", scrambleText))
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(context, "公式已复制", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
        ) {
            AnimatedContent(
                targetState = scrambleText,
                modifier = Modifier.align(Alignment.Center),
                transitionSpec = {
                    if (wasRunning) {
                        EnterTransition.None togetherWith ExitTransition.None
                    } else {
                        val enter = slideInHorizontally { it / 2 } + fadeIn(animationSpec = tween(300))
                        val exit = slideOutHorizontally { -it / 2 } + fadeOut(animationSpec = tween(300))
                        (enter togetherWith exit).using(SizeTransform(clip = false))
                    }
                },
                label = "scramble_content"
            ) { text ->
                Surface(
                    shape = ShapeDefaults.Large,
                    color = adaptiveContainerColor,
                    border = BorderStroke(1.dp, adaptiveContentColor.copy(alpha = 0.12f)),
                    tonalElevation = 1.dp
                ) {
                    Text(
                        text = text,
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = adaptiveFontSize,
                            textAlign = TextAlign.Center,
                            lineHeight = adaptiveFontSize * 1.35f,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        color = adaptiveContentColor,
                        softWrap = true,
                        overflow = TextOverflow.Visible
                    )
                }
            }
        }

        val previewAlign = if (isLandscape) Alignment.BottomEnd else Alignment.BottomCenter
        Box(
            modifier = Modifier
                .align(previewAlign)
                .padding(bottom = if (isLandscape) 0.dp else 16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onShowFullPreview()
                }
        ) {
            ScramblePreview(cubeState = cubeState, cellSize = adaptiveCellSize)
        }
    }
}