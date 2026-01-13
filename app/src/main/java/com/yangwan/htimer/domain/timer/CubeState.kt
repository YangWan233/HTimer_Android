package com.yangwan.htimer.domain.timer

enum class CubeFace { U, D, L, R, F, B }

class CubeState {
    var faces = mutableMapOf(
        CubeFace.U to Array(3) { IntArray(3) },
        CubeFace.D to Array(3) { IntArray(3) { 1 } },
        CubeFace.L to Array(3) { IntArray(3) { 2 } },
        CubeFace.R to Array(3) { IntArray(3) { 3 } },
        CubeFace.F to Array(3) { IntArray(3) { 4 } },
        CubeFace.B to Array(3) { IntArray(3) { 5 } }
    )

    fun applyScramble(scramble: String) {
        if (scramble.isBlank()) return
        reset()

        scramble.split(" ").filter { it.isNotBlank() }.forEach { move ->
            val face = move[0]
            val times = when {
                move.endsWith("'") -> 3
                move.endsWith("2") -> 2
                else -> 1
            }
            repeat(times) { rotate(face.toString()) }
        }
    }

    private fun reset() {
        faces[CubeFace.U]!!.forEach { it.fill(0) }
        faces[CubeFace.D]!!.forEach { it.fill(1) }
        faces[CubeFace.L]!!.forEach { it.fill(2) }
        faces[CubeFace.R]!!.forEach { it.fill(3) }
        faces[CubeFace.F]!!.forEach { it.fill(4) }
        faces[CubeFace.B]!!.forEach { it.fill(5) }
    }

    private fun rotate(move: String) {
        val f = CubeFace.valueOf(move)

        val currentFace = faces[f]!!
        val nextFace = Array(3) { IntArray(3) }
        for (r in 0..2) {
            for (c in 0..2) {
                nextFace[c][2 - r] = currentFace[r][c]
            }
        }
        for (i in 0..2) faces[f]!![i] = nextFace[i].copyOf()

        when (f) {
            CubeFace.U -> {
                val temp = (0..2).map { faces[CubeFace.F]!![0][it] }
                (0..2).forEach { faces[CubeFace.F]!![0][it] = faces[CubeFace.R]!![0][it] }
                (0..2).forEach { faces[CubeFace.R]!![0][it] = faces[CubeFace.B]!![0][it] }
                (0..2).forEach { faces[CubeFace.B]!![0][it] = faces[CubeFace.L]!![0][it] }
                (0..2).forEach { faces[CubeFace.L]!![0][it] = temp[it] }
            }

            CubeFace.D -> {
                val temp = (0..2).map { faces[CubeFace.F]!![2][it] }
                (0..2).forEach { faces[CubeFace.F]!![2][it] = faces[CubeFace.L]!![2][it] }
                (0..2).forEach { faces[CubeFace.L]!![2][it] = faces[CubeFace.B]!![2][it] }
                (0..2).forEach { faces[CubeFace.B]!![2][it] = faces[CubeFace.R]!![2][it] }
                (0..2).forEach { faces[CubeFace.R]!![2][it] = temp[it] }
            }

            CubeFace.L -> {
                val temp = (0..2).map { faces[CubeFace.U]!![it][0] }
                (0..2).forEach { faces[CubeFace.U]!![it][0] = faces[CubeFace.B]!![2 - it][2] }
                (0..2).forEach { faces[CubeFace.B]!![2 - it][2] = faces[CubeFace.D]!![it][0] }
                (0..2).forEach { faces[CubeFace.D]!![it][0] = faces[CubeFace.F]!![it][0] }
                (0..2).forEach { faces[CubeFace.F]!![it][0] = temp[it] }
            }

            CubeFace.R -> {
                val temp = (0..2).map { faces[CubeFace.U]!![it][2] }
                (0..2).forEach { faces[CubeFace.U]!![it][2] = faces[CubeFace.F]!![it][2] }
                (0..2).forEach { faces[CubeFace.F]!![it][2] = faces[CubeFace.D]!![it][2] }
                (0..2).forEach { faces[CubeFace.D]!![it][2] = faces[CubeFace.B]!![2 - it][0] }
                (0..2).forEach { faces[CubeFace.B]!![2 - it][0] = temp[it] }
            }

            CubeFace.F -> {
                val temp = (0..2).map { faces[CubeFace.U]!![2][it] }
                (0..2).forEach { faces[CubeFace.U]!![2][it] = faces[CubeFace.L]!![2 - it][2] }
                (0..2).forEach { faces[CubeFace.L]!![2 - it][2] = faces[CubeFace.D]!![0][2 - it] }
                (0..2).forEach { faces[CubeFace.D]!![0][2 - it] = faces[CubeFace.R]!![it][0] }
                (0..2).forEach { faces[CubeFace.R]!![it][0] = temp[it] }
            }

            CubeFace.B -> {
                val temp = (0..2).map { faces[CubeFace.U]!![0][it] }
                (0..2).forEach { faces[CubeFace.U]!![0][it] = faces[CubeFace.R]!![it][2] }
                (0..2).forEach { faces[CubeFace.R]!![it][2] = faces[CubeFace.D]!![2][2 - it] }
                (0..2).forEach { faces[CubeFace.D]!![2][2 - it] = faces[CubeFace.L]!![2 - it][0] }
                (0..2).forEach { faces[CubeFace.L]!![2 - it][0] = temp[it] }
            }
        }
    }
}