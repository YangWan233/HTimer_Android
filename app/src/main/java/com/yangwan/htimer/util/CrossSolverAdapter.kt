package com.yangwan.htimer.util

object CrossSolverAdapter {

    private fun transformScramble(scramble: List<String>, rotation: String): Array<String> {
        return scramble.map { move ->
            val face = move.take(1)
            val suffix = move.substring(1)

            val (newFace, newSuffix) = when (rotation) {
                "x2" -> mapOf("U" to "D", "D" to "U", "F" to "B", "B" to "F").getOrDefault(face, face) to suffix
                "x'" -> mapOf("U" to "F", "F" to "D", "D" to "B", "B" to "U").getOrDefault(face, face) to suffix
                "x"  -> mapOf("U" to "B", "B" to "D", "D" to "F", "F" to "U").getOrDefault(face, face) to suffix
                "z"  -> mapOf("U" to "R", "R" to "D", "D" to "L", "L" to "U").getOrDefault(face, face) to suffix
                "z'" -> mapOf("U" to "L", "L" to "D", "D" to "R", "R" to "U").getOrDefault(face, face) to suffix
                else -> face to suffix
            }
            newFace + newSuffix
        }.toTypedArray()
    }

    private val faceConfigs = listOf(
        Triple("D (黄)", "", ""),
        Triple("U (白)", "x2", "x2"),
        Triple("F (绿)", "x'", "x'"),
        Triple("B (蓝)", "x", "x"),
        Triple("L (橙)", "z'", "z'"),
        Triple("R (红)", "z", "z")
    )

    fun getAllFaceSolutions(scrambleStr: String): List<String> {
        if (scrambleStr.isBlank()) return emptyList()
        val originalScramble = scrambleStr.split(" ").filter { it.isNotBlank() }

        return faceConfigs.map { (name, transform, displayPrefix) ->
            val mappedScramble = transformScramble(originalScramble, transform)
            val state = State.id.applySequence(mappedScramble)
            val solved = RubiksCubeCrossSolver.solve(state)
            val solStr = if (solved.isNotEmpty()) solved[0].joinToString(" ") else "无解"
            val prefix = if (displayPrefix.isNotEmpty()) "$displayPrefix " else ""
            "$name: $prefix$solStr"
        }
    }
}