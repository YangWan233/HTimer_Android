package com.yangwan.htimer.util

class State(
    val cornersPermutation: ByteArray,
    val cornersOrientation: ByteArray,
    val edgesPermutation: ByteArray,
    val edgesOrientation: ByteArray
) {
    fun multiply(move: State): State {
        val cp = ByteArray(8)
        val co = ByteArray(8)
        for (i in 0..7) {
            val p = move.cornersPermutation[i].toInt()
            cp[i] = this.cornersPermutation[p]
            co[i] = ((this.cornersOrientation[p] + move.cornersOrientation[i]) % 3).toByte()
        }

        val ep = ByteArray(12)
        val eo = ByteArray(12)
        for (i in 0..11) {
            val p = move.edgesPermutation[i].toInt()
            ep[i] = this.edgesPermutation[p]
            eo[i] = ((this.edgesOrientation[p] + move.edgesOrientation[i]) % 2).toByte()
        }
        return State(cp, co, ep, eo)
    }

    fun applySequence(sequence: Array<String>): State {
        var state = this
        sequence.forEach { move ->
            moves[move]?.let { state = state.multiply(it) }
        }
        return state
    }

    companion object {
        val moves = mutableMapOf<String, State>()
        val id: State by lazy {
            State(
                IndexMapping.indexToPermutation(0, 8),
                IndexMapping.indexToOrientation(0, 3, 8),
                IndexMapping.indexToPermutation(0, 12),
                IndexMapping.indexToOrientation(0, 2, 12)
            )
        }

        init {
            val moveU = State(byteArrayOf(3, 0, 1, 2, 4, 5, 6, 7), ByteArray(8), byteArrayOf(0, 1, 2, 3, 7, 4, 5, 6, 8, 9, 10, 11), ByteArray(12))
            val moveD = State(byteArrayOf(0, 1, 2, 3, 5, 6, 7, 4), ByteArray(8), byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 9, 10, 11, 8), ByteArray(12))
            val moveL = State(byteArrayOf(4, 1, 2, 0, 7, 5, 6, 3), byteArrayOf(2, 0, 0, 1, 1, 0, 0, 2), byteArrayOf(11, 1, 2, 7, 4, 5, 6, 0, 8, 9, 10, 3), ByteArray(12))
            val moveR = State(byteArrayOf(0, 2, 6, 3, 4, 1, 5, 7), byteArrayOf(0, 1, 2, 0, 0, 2, 1, 0), byteArrayOf(0, 5, 9, 3, 4, 2, 6, 7, 8, 1, 10, 11), ByteArray(12))
            val moveF = State(byteArrayOf(0, 1, 3, 7, 4, 5, 2, 6), byteArrayOf(0, 0, 1, 2, 0, 0, 2, 1), byteArrayOf(0, 1, 6, 10, 4, 5, 3, 7, 8, 9, 2, 11), byteArrayOf(0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 1, 0))
            val moveB = State(byteArrayOf(1, 5, 2, 3, 0, 4, 6, 7), byteArrayOf(1, 2, 0, 0, 2, 1, 0, 0), byteArrayOf(4, 8, 2, 3, 1, 5, 6, 7, 0, 9, 10, 11), byteArrayOf(1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0))

            fun addMove(name: String, m: State) {
                moves[name] = m
                moves["${name}2"] = m.multiply(m)
                moves["${name}'"] = m.multiply(m).multiply(m)
            }
            addMove("U", moveU); addMove("D", moveD); addMove("L", moveL)
            addMove("R", moveR); addMove("F", moveF); addMove("B", moveB)
        }
    }
}