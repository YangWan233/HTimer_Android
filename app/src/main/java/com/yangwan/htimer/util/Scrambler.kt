package com.yangwan.htimer.util

import kotlin.random.Random

object Scrambler {
    private val moves = arrayOf("U", "D", "L", "R", "F", "B")
    private val types = arrayOf("", "'", "2")

    fun next(length: Int = 20): String {
        val list = mutableListOf<String>()
        var last = -1

        repeat(length) {
            var curr: Int
            do {
                curr = Random.nextInt(moves.size)
            } while (curr == last)

            list.add(moves[curr] + types[Random.nextInt(types.size)])
            last = curr
        }
        return list.joinToString(" ")
    }
}