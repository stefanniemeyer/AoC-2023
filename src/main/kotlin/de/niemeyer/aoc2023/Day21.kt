/**
 * Advent of Code 2023, Day 21: Step Counter
 * Problem Description: https://adventofcode.com/2023/day/21
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.grid.Grid
import de.niemeyer.aoc.grid.GridCellScreen
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*

fun main() {
    fun part1(input: List<String>, maxSteps: Int = 6): Int =
        Day21(input).solve(maxSteps)

    fun part2(input: List<String>): Int =
        TODO()

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput, 6) == 16)
    executeAndCheck(1, 3_746) {
        part1(puzzleInput, 64)
    }

//    check(part2(testInput) == 0)
//    executeAndCheck(2, 0) {
//        part2(puzzleInput)
//    }
}

class Day21(val input: List<String>) {
    var height: Int = 0
    var width: Int = 0
    var rocks: Set<GridCellScreen> = emptySet()
    var startPos: GridCellScreen = GridCellScreen.ORIGIN

    fun solve(maxSteps: Int): Int {
        height = input.size
        width = input.first().length
        rocks = Grid.of(input, GridCellScreen(1, 1), ignoreChars = listOf('.', 'S')).gridMap.keys.toSet()
        val start = Grid.of(
            input,
            GridCellScreen(1, 1),
            ignoreChars = listOf('.', '#'),
            relevantChars = listOf('S')
        ).gridMap.keys
        if (start.size != 1) error("${start.size} 'S' found, exactly 1 expected")
        startPos = start.first()
        val x = dfs(maxSteps)
        return x.size
    }

    fun dfs(maxSteps: Int): Set<GridCellScreen> {
        val foundPaths: MutableSet<GridCellScreen> = mutableSetOf()
        val visited = mutableSetOf<GridCellScreen>()
        val queue = ArrayDeque<IndexedValue<GridCellScreen>>()
        visited += startPos
        queue += IndexedValue(0, startPos)
        while (queue.isNotEmpty()) {
            val pos = queue.removeFirst()
            if (pos.index % 2 == 0) {
                foundPaths += pos.value
            }
            if (pos.index == maxSteps) {
                continue
            }

            val nextPositions =
                pos.value.axisNeighbors
                    .filterNot { rocks.contains(it) || it == pos.value || visited.contains(it) }
                    .map { IndexedValue(pos.index + 1, it) }
            visited += nextPositions.map { it.value }
            queue += nextPositions
        }
        return foundPaths
    }
}
