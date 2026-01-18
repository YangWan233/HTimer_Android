package com.yangwan.htimer.util

object RubiksCubeCrossSolver {
    private val moveNames = arrayOf("U", "U2", "U'", "D", "D2", "D'", "L", "L2", "L'", "R", "R2", "R'", "F", "F2", "F'", "B", "B2", "B'")
    private val moves = Array(moveNames.size) { State.moves[moveNames[it]]!! }
    private val goalP: Int
    private val goalO: Int
    private val pMove = Array(495 * 24) { IntArray(moves.size) }
    private val oMove = Array(495 * 16) { IntArray(moves.size) }
    private val pDist = ByteArray(495 * 24) { -1 }
    private val oDist = ByteArray(495 * 16) { -1 }

    init {
        val g = stateToIndices(State.id)
        goalP = g[0] * 24 + g[1]
        goalO = g[0] * 16 + g[2]

        for (i in 0 until 495) {
            for (j in 0 until 24) {
                val s = indicesToState(intArrayOf(i, j, 0))
                for (k in moves.indices) {
                    val res = stateToIndices(s.multiply(moves[k]))
                    pMove[i * 24 + j][k] = res[0] * 24 + res[1]
                }
            }
            for (j in 0 until 16) {
                val s = indicesToState(intArrayOf(i, 0, j))
                for (k in moves.indices) {
                    val res = stateToIndices(s.multiply(moves[k]))
                    oMove[i * 16 + j][k] = res[0] * 16 + res[2]
                }
            }
        }
        fillDist(pDist, pMove, goalP)
        fillDist(oDist, oMove, goalO)
    }

    private fun fillDist(dist: ByteArray, move: Array<IntArray>, goal: Int) {
        dist[goal] = 0
        var d = 0.toByte(); var visited = 1
        while (visited < dist.size) {
            for (i in dist.indices) {
                if (dist[i] != d) continue
                for (k in moves.indices) {
                    val n = move[i][k]
                    if (dist[n] == (-1).toByte()) { dist[n] = (d + 1).toByte(); visited++ }
                }
            }
            d++
        }
    }

    private fun stateToIndices(s: State): IntArray {
        val comb = BooleanArray(12) { s.edgesPermutation[it] >= 8 }
        val p = ByteArray(4); val o = ByteArray(4); var n = 0
        for (i in 0..11) if (comb[i]) {
            p[n] = (s.edgesPermutation[i] - 8).toByte()
            o[n] = s.edgesOrientation[i]; n++
        }
        return intArrayOf(IndexMapping.combinationToIndex(comb, 4), IndexMapping.permutationToIndex(p), IndexMapping.orientationToIndex(o, 2))
    }

    private fun indicesToState(indices: IntArray): State {
        val comb = IndexMapping.indexToCombination(indices[0], 4, 12)
        val p = IndexMapping.indexToPermutation(indices[1], 4)
        val o = IndexMapping.indexToOrientation(indices[2], 2, 4)
        val ep = ByteArray(12); val eo = ByteArray(12); var nS = 0; var nO = 0
        for (i in 0..11) {
            if (comb[i]) {
                ep[i] = (p[nS] + 8).toByte(); eo[i] = o[nS]; nS++
            } else {
                ep[i] = nO.toByte(); eo[i] = 0; nO++
            }
        }
        return State(State.id.cornersPermutation, State.id.cornersOrientation, ep, eo)
    }

    fun solve(state: State): List<Array<String>> {
        val idx = stateToIndices(state)
        val cp = idx[0] * 24 + idx[1]; val co = idx[0] * 16 + idx[2]
        val sol = mutableListOf<Array<String>>()
        var depth = 0
        while (true) {
            if (search(cp, co, depth, IntArray(depth), sol)) return sol
            if (depth++ > 8) return sol
        }
    }

    private fun search(p: Int, o: Int, d: Int, path: IntArray, sol: MutableList<Array<String>>): Boolean {
        if (d == 0) {
            if (p == goalP && o == goalO) {
                sol.add(Array(path.size) { moveNames[path[it]] })
                return true
            }
            return false
        }
        if (pDist[p] > d || oDist[o] > d) return false
        for (i in moves.indices) {
            if (path.size > d && i / 3 == path[path.size - d - 1] / 3) continue
            path[path.size - d] = i
            if (search(pMove[p][i], oMove[o][i], d - 1, path, sol)) return true
        }
        return false
    }
}