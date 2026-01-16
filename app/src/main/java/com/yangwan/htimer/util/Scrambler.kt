package com.yangwan.htimer.util

import org.kociemba.twophase.Search
import org.kociemba.twophase.Tools

object Scrambler {
    // 1. 持久化 Search 实例，避免重复创建对象造成的内存抖动
    private val searchInstance = Search()

    /**
     * 生成随机状态打乱公式 (Random State Scramble)
     * 符合 WCA 竞赛标准的打乱逻辑
     */
    fun next(): String {
        // 2. 生成一个物理意义上完全随机的魔方状态字符串
        val randomCubeState = Tools.randomCube()

        // 3. 求解该状态。返回的公式即为将复原魔方打乱至该状态的步骤
        // 参数：随机状态, 最大深度(21), 超时(1000ms), 最小深度(0), 输出格式(0)
        val result = searchInstance.solution(randomCubeState, 21, 1000, 0, 0)

        return result.trim()
    }
}