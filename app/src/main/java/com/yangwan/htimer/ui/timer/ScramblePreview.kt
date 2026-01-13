package com.yangwan.htimer.ui.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yangwan.htimer.domain.timer.CubeFace
import com.yangwan.htimer.domain.timer.CubeState

/**
 * 魔方打乱预览组件：采用标准的十字展开图 (Net View) 布局
 */
@Composable
fun ScramblePreview(
    cubeState: CubeState,
    modifier: Modifier = Modifier,
    cellSize: Dp = 6.dp
) {
    val cellSpacing = 0.2.dp
    val faceSpacing = 2.dp

    val colorMap = listOf(
        Color(0xFFFFFFFF), // 0: White (U)
        Color(0xFFFFFF00), // 1: Yellow (D)
        Color(0xFFFFAA00), // 2: Orange (L)
        Color(0xFFFF0000), // 3: Red (R)
        Color(0xFF00DD00), // 4: Green (F)
        Color(0xFF0000FF)  // 5: Blue (B)
    )

    val faceWidth = (cellSize + cellSpacing * 2) * 3

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 顶层 (U)
        Row {
            Spacer(Modifier.width(faceWidth + faceSpacing))
            FaceGrid(cubeState.faces[CubeFace.U]!!, colorMap, cellSize, cellSpacing)
            Spacer(Modifier.width((faceWidth + faceSpacing) * 2))
        }

        Spacer(Modifier.height(faceSpacing))

        // 2. 中间层 (L, F, R, B)
        Row(verticalAlignment = Alignment.CenterVertically) {
            FaceGrid(cubeState.faces[CubeFace.L]!!, colorMap, cellSize, cellSpacing)
            Spacer(Modifier.width(faceSpacing))
            FaceGrid(cubeState.faces[CubeFace.F]!!, colorMap, cellSize, cellSpacing)
            Spacer(Modifier.width(faceSpacing))
            FaceGrid(cubeState.faces[CubeFace.R]!!, colorMap, cellSize, cellSpacing)
            Spacer(Modifier.width(faceSpacing))
            FaceGrid(cubeState.faces[CubeFace.B]!!, colorMap, cellSize, cellSpacing)
        }

        Spacer(Modifier.height(faceSpacing))

        // 3. 底层 (D)
        Row {
            Spacer(Modifier.width(faceWidth + faceSpacing))
            FaceGrid(cubeState.faces[CubeFace.D]!!, colorMap, cellSize, cellSpacing)
            Spacer(Modifier.width((faceWidth + faceSpacing) * 2))
        }
    }
}

/**
 * 渲染单个面的 3x3 网格
 */
@Composable
private fun FaceGrid(
    face: Array<IntArray>,
    colorMap: List<Color>,
    size: Dp,
    spacing: Dp
) {
    val strokeWidth = if (size > 10.dp) 1.2.dp else 0.8.dp
    val borderColor = Color.Black.copy(alpha = 0.7f)

    Column {
        face.forEach { row ->
            Row {
                row.forEach { colorIndex ->
                    Box(
                        modifier = Modifier
                            .padding(spacing)
                            .size(size)
                            .clip(RoundedCornerShape(1.dp))
                            .background(colorMap[colorIndex])
                            .border(strokeWidth, borderColor, RoundedCornerShape(1.dp))
                    )
                }
            }
        }
    }
}