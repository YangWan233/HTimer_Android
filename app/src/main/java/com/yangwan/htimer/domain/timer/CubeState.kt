package com.yangwan.htimer.domain.timer

enum class CubeFace { U, D, L, R, F, B }

class CubeState {
    var faces = mutableMapOf(
        CubeFace.U to Array(3) { IntArray(3) { 0 } }, // 0: 白
        CubeFace.D to Array(3) { IntArray(3) { 1 } }, // 1: 黄
        CubeFace.L to Array(3) { IntArray(3) { 2 } }, // 2: 橙
        CubeFace.R to Array(3) { IntArray(3) { 3 } }, // 3: 红
        CubeFace.F to Array(3) { IntArray(3) { 4 } }, // 4: 绿
        CubeFace.B to Array(3) { IntArray(3) { 5 } }  // 5: 蓝
    )

    fun applyFacelets(facelets: String) {
        if (facelets.length != 54) return

        val facesOrder = arrayOf(
            CubeFace.U, CubeFace.R, CubeFace.F,
            CubeFace.D, CubeFace.L, CubeFace.B
        )

        var charIndex = 0
        for (face in facesOrder) {
            val grid = faces[face]!!
            for (row in 0..2) {
                for (col in 0..2) {
                    grid[row][col] = when (facelets[charIndex]) {
                        'U' -> 0
                        'D' -> 1
                        'L' -> 2
                        'R' -> 3
                        'F' -> 4
                        'B' -> 5
                        else -> 0
                    }
                    charIndex++
                }
            }
        }
    }
}