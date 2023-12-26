/**
 * Advent of Code 2023, Day 21: Step Counter
 * Problem Description: https://adventofcode.com/2023/day/21
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.grid.Grid
import de.niemeyer.aoc.grid.GridCellScreen
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*
import java.util.*

fun solve21(input: List<Long>): Long =
    recursiveDiff(input).map { it.last() }
        .reversed()
        .fold(0L) { acc, num ->
            num + acc
        }

fun main() {
    fun part1(input: List<String>, maxSteps: List<Int>): Long =
        Day21(input).solve(maxSteps).first()

    fun part2(input: List<String>, maxSteps: Int): Long {
        val cycleLength = (input.size * 2)
        val cycleOffset = maxSteps % cycleLength
        val initialCycles = 5
        val neededSteps = (2..initialCycles).map { cycleOffset + it * cycleLength }
        val firstNums = Day21(input).solve(neededSteps)

        val restCycles = (maxSteps - cycleOffset) / cycleLength - firstNums.size - 1
        var nums = firstNums.toList()
        for (cycle in 1..restCycles) {
            val newNum = solve21(nums)
            nums = (nums.takeLast(initialCycles) + newNum)
        }

        return nums.last()
    }

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput, listOf(6)) == 16L)
    check(part1(testInput, listOf(10)) == 50L)
    executeAndCheck(1, 3_746L) {
        part1(puzzleInput, listOf(64))
    }

    check(part2(testInput, 5000) == 16_733_044L)
    executeAndCheck(2, 623_540_829_615_589L) {
        part2(puzzleInput, 26_501_365)
    }
}

class Day21(val input: List<String>) {
    private var height = 0
    private var width = 0
    private var rocks = emptySet<GridCellScreen>()
    private var startPos = GridCellScreen.ORIGIN

    fun solve(maxSteps: List<Int>): List<Long> {
        height = input.size
        width = input.first().length
        rocks = Grid.of(input, ignoreChars = listOf('.', 'S')).gridMap.keys.toSet()
        val start = Grid.of(input, ignoreChars = listOf('.', '#'), relevantChars = listOf('S')).gridMap.keys
        if (start.size != 1) error("${start.size} 'S' found, exactly 1 expected")
        startPos = start.first()
        return dfs(maxSteps)
    }

    private fun isRock(pos: GridCellScreen): Boolean {
        val newPos = GridCellScreen(pos.row.mod(height), pos.column.mod(width))
        return rocks.contains(newPos)
    }

    private fun dfs(relevantSteps: List<Int>): List<Long> {
        val foundPaths: MutableSet<GridCellScreen> = mutableSetOf()
        val visited = mutableSetOf<GridCellScreen>()
        val queue = PriorityQueue<IndexedValue<GridCellScreen>>(compareBy { it.index })
        val maxSteps = relevantSteps.maxOf { it }
        val evenOdd = maxSteps % 2
        visited += startPos
        queue += IndexedValue(0, startPos)
        val results = mutableMapOf<Int, Long>()
        while (queue.isNotEmpty()) {
            val pos = queue.poll()
            if (pos.index % 2 == evenOdd) {
                foundPaths += pos.value
            }
            results[pos.index] = foundPaths.size.toLong()
            if (pos.index == maxSteps) {
                continue
            }

            val nextPositions =
                pos.value.axisNeighbors
                    .filterNot { isRock(it) || it == pos.value || visited.contains(it) }
                    .map { IndexedValue(pos.index + 1, it) }
            visited += nextPositions.map { it.value }
            queue += nextPositions
        }
        return results.filter { it.key in relevantSteps }.map { it.value }
    }
}
