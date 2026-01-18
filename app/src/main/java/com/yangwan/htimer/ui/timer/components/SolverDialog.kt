package com.yangwan.htimer.ui.timer.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yangwan.htimer.util.CrossSolverAdapter

@Composable
fun CrossSolutionDialog(
    scramble: String,
    onDismiss: () -> Unit
) {
    val solutions = remember(scramble) { CrossSolverAdapter.getAllFaceSolutions(scramble) }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("了解", fontWeight = FontWeight.ExtraBold)
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Lightbulb,
                contentDescription = null,
                tint = Color(0xFFFFD700)
            )
        },
        title = {
            Text(
                text = "六色底解法参考",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                solutions.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { entry ->
                            SolutionGridCard(
                                entry = entry,
                                modifier = Modifier.weight(1f),
                                onLongClick = { moves ->
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("CrossSolution", moves)
                                    cm.setPrimaryClip(clip)
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    Toast.makeText(context, "公式已复制", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        },
        shape = ShapeDefaults.ExtraLarge,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

@Composable
private fun SolutionGridCard(
    entry: String,
    modifier: Modifier = Modifier,
    onLongClick: (String) -> Unit
) {
    val parts = entry.split(": ")
    val faceName = parts.getOrNull(0) ?: ""
    val moves = parts.getOrNull(1) ?: ""

    val faceColor = remember(faceName) {
        when {
            faceName.contains("白") -> Color.White
            faceName.contains("黄") -> Color(0xFFFFEB3B)
            faceName.contains("绿") -> Color(0xFF4CAF50)
            faceName.contains("蓝") -> Color(0xFF2196F3)
            faceName.contains("橙") -> Color(0xFFFF9800)
            faceName.contains("红") -> Color(0xFFF44336)
            else -> Color.Gray
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = if (isPressed) Spring.StiffnessMediumLow else Spring.StiffnessLow
        ),
        label = "smooth_scale"
    )

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .pointerInput(moves) {
                detectTapGestures(
                    onPress = { offset ->
                        val press = PressInteraction.Press(offset)
                        interactionSource.emit(press)
                        tryAwaitRelease()
                        interactionSource.emit(PressInteraction.Release(press))
                    },
                    onLongPress = { onLongClick(moves) }
                )
            },
        shape = ShapeDefaults.Large,
        color = faceColor.copy(alpha = 0.08f).compositeOver(MaterialTheme.colorScheme.surfaceContainerHighest),
        tonalElevation = if (isPressed) 0.dp else 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(width = 24.dp, height = 4.dp)
                    .background(faceColor.copy(alpha = 0.8f), CircleShape)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = faceName,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = moves,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                    lineHeight = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                minLines = 2
            )
        }
    }
}