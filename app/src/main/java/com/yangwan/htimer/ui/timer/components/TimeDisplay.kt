package com.yangwan.htimer.ui.timer.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun TimeDisplay(
    timeMillis: Long,
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false
) {
    val colorScheme = MaterialTheme.colorScheme

    val morphProgress by animateFloatAsState(
        targetValue = if (isRunning) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "timer_morph"
    )

    val timerTextColor = if (isRunning) colorScheme.primary else colorScheme.onSurface

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val screenWidthDp = maxWidth
        val sizeFactor = if (isLandscape) 0.07f else 0.11f
        val cellWidth = screenWidthDp * sizeFactor
        val baseFontSize = (cellWidth.value * 1.6f).sp

        val minsStr = "%02d".format(timeMillis / 60000)
        val secsInMin = (timeMillis % 60000) / 1000
        val millisStr = "%03d".format(timeMillis % 1000)

        Row(
            modifier = Modifier.graphicsLayer { clip = false },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Box(modifier = Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints.copy(minWidth = 0))
                    val lerpWidth = (placeable.width * (1f - morphProgress)).toInt()
                    layout(lerpWidth, placeable.height) {
                        placeable.placeRelative(lerpWidth - placeable.width, 0)
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
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints.copy(minWidth = 0))
                    val lerpWidth = (placeable.width * (1f - morphProgress)).toInt()
                    layout(lerpWidth, placeable.height) {
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
    }
}

@Composable
private fun StaticCell(width: Dp, char: String, fontSize: TextUnit, textColor: Color) {
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