package com.yangwan.htimer.ui.timer.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yangwan.htimer.domain.timer.CubeState
import com.yangwan.htimer.ui.timer.ScramblePreview

@Composable
fun PreviewDialog(cubeState: CubeState, onDismiss: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            color = colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .clickable(
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