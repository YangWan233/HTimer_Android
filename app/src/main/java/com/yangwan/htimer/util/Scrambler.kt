package com.yangwan.htimer.util

import org.kociemba.twophase.Search
import org.kociemba.twophase.Tools

object Scrambler {

    private val searchInstance = Search()

    fun next(): String {
        val randomCubeState = Tools.randomCube()
        val result = searchInstance.solution(randomCubeState, 21, 1000, 0, 0)
        return result.trim()
    }
}
