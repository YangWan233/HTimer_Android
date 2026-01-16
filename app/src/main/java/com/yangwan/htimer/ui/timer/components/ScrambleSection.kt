package com.yangwan.htimer.ui.timer.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yangwan.htimer.domain.timer.CubeState
import com.yangwan.htimer.ui.timer.ScramblePreview

@Composable
fun ScrambleCardSection(
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
                    (scaleIn(initialScale = 0.92f, animationSpec = androidx.compose.animation.core.tween(250)) + fadeIn(androidx.compose.animation.core.tween(250)))
                        .togetherWith(scaleOut(targetScale = 0.92f, animationSpec = androidx.compose.animation.core.tween(200)) + fadeOut(androidx.compose.animation.core.tween(200)))
                } else {
                    (slideInHorizontally(androidx.compose.animation.core.tween(400)) { it / 4 } + scaleIn(initialScale = 0.85f, animationSpec = androidx.compose.animation.core.tween(400)) + fadeIn(androidx.compose.animation.core.tween(400)))
                        .togetherWith(slideOutHorizontally(androidx.compose.animation.core.tween(350)) { -it / 2 } + scaleOut(targetScale = 0.9f, animationSpec = androidx.compose.animation.core.tween(350)) + fadeOut(androidx.compose.animation.core.tween(250)))
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
                // 这里保持你所有的 TextStyle 参数不动
                Text(
                    text = targetScramble,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.sp,
                        fontFeatureSettings = "tnum, mono",
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        ),
                        lineHeight = 20.sp
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