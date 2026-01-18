package com.yangwan.htimer.util

object IndexMapping {

    fun permutationToIndex(permutation: ByteArray): Int {
        var index = 0
        for (i in 0 until permutation.size - 1) {
            index *= (permutation.size - i)
            for (j in i + 1 until permutation.size) {
                if (permutation[i] > permutation[j]) {
                    index++
                }
            }
        }
        return index
    }

    fun indexToPermutation(index: Int, length: Int): ByteArray {
        var idx = index
        val permutation = ByteArray(length)
        permutation[length - 1] = 0
        for (i in length - 2 downTo 0) {
            permutation[i] = (idx % (length - i)).toByte()
            idx /= (length - i)
            for (j in i + 1 until length) {
                if (permutation[j] >= permutation[i]) {
                    permutation[j]++
                }
            }
        }
        return permutation
    }

    fun evenPermutationToIndex(permutation: ByteArray): Int {
        var index = 0
        for (i in 0 until permutation.size - 2) {
            index *= (permutation.size - i)
            for (j in i + 1 until permutation.size) {
                if (permutation[i] > permutation[j]) {
                    index++
                }
            }
        }
        return index
    }

    fun indexToEvenPermutation(index: Int, length: Int): ByteArray {
        var idx = index
        var sum = 0
        val permutation = ByteArray(length)
        permutation[length - 1] = 1
        permutation[length - 2] = 0
        for (i in length - 3 downTo 0) {
            permutation[i] = (idx % (length - i)).toByte()
            sum += permutation[i].toInt()
            idx /= (length - i)
            for (j in i + 1 until length) {
                if (permutation[j] >= permutation[i]) {
                    permutation[j]++
                }
            }
        }
        if (sum % 2 != 0) {
            val temp = permutation[length - 1]
            permutation[length - 1] = permutation[length - 2]
            permutation[length - 2] = temp
        }
        return permutation
    }

    fun orientationToIndex(orientation: ByteArray, nValues: Int): Int {
        var index = 0
        for (i in orientation.indices) {
            index = nValues * index + (orientation[i].toInt() and 0xFF)
        }
        return index
    }

    fun indexToOrientation(index: Int, nValues: Int, length: Int): ByteArray {
        var idx = index
        val orientation = ByteArray(length)
        for (i in length - 1 downTo 0) {
            orientation[i] = (idx % nValues).toByte()
            idx /= nValues
        }
        return orientation
    }

    fun zeroSumOrientationToIndex(orientation: ByteArray, nValues: Int): Int {
        var index = 0
        for (i in 0 until orientation.size - 1) {
            index = nValues * index + (orientation[i].toInt() and 0xFF)
        }
        return index
    }

    fun indexToZeroSumOrientation(index: Int, nValues: Int, length: Int): ByteArray {
        var idx = index
        val orientation = ByteArray(length)
        var currentSum = 0
        for (i in length - 2 downTo 0) {
            orientation[i] = (idx % nValues).toByte()
            currentSum += orientation[i].toInt()
            idx /= nValues
        }
        orientation[length - 1] =
            ((nValues - (currentSum % nValues)) % nValues).toByte()
        return orientation
    }

    private fun nChooseK(n: Int, k: Int): Int {
        if (k < 0 || k > n) return 0
        var value = 1
        for (i in 0 until k) {
            value *= (n - i)
        }
        for (i in 0 until k) {
            value /= (k - i)
        }
        return value
    }

    fun combinationToIndex(combination: BooleanArray, k: Int): Int {
        var index = 0
        var currentK = k
        for (i in combination.size - 1 downTo 0) {
            if (currentK <= 0) break
            if (combination[i]) {
                index += nChooseK(i, currentK)
                currentK--
            }
        }
        return index
    }

    fun indexToCombination(index: Int, k: Int, length: Int): BooleanArray {
        var idx = index
        var currentK = k
        val combination = BooleanArray(length)
        for (i in length - 1 downTo 0) {
            if (currentK < 0) break
            val nck = nChooseK(i, currentK)
            if (idx >= nck) {
                combination[i] = true
                idx -= nck
                currentK--
            }
        }
        return combination
    }
}
