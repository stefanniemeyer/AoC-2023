/**
 * Advent of Code 2023, Day 14: Parabolic Reflector Dish
 * Problem Description: https://adventofcode.com/2023/day/14
 */

@file:Suppress("unused")

package de.niemeyer.aoc2023

import de.niemeyer.aoc.direction.DirectionScreen
import de.niemeyer.aoc.grid.Grid
import de.niemeyer.aoc.grid.GridCellScreen
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName

fun String.compress(): String {
    val c = this.count('O'::equals)
    return StringBuilder().apply {
        repeat(c) { append('O') }
        repeat(this@compress.length - c) { append('.') }
    }.toString()
}

var height = 0
var width = 0
var cubeRocks = setOf<GridCellScreen>()

val dishCache = mutableMapOf<String, Int>()

fun Set<GridCellScreen>.compress(facing: DirectionScreen): Set<GridCellScreen> {
    val result = mutableSetOf<GridCellScreen>()
    val rowProgression = IntProgression.fromClosedRange(1, height, 1)
    val columnProgression = IntProgression.fromClosedRange(1, width, 1)
    val transFunc: (Int, Int) -> GridCellScreen

    when (facing) {
        DirectionScreen.Up -> transFunc = { r, c -> GridCellScreen(r, c) }
        DirectionScreen.Right -> transFunc = { r, c -> GridCellScreen(c, height - r + 1) }
        DirectionScreen.Down -> transFunc = { r, c -> GridCellScreen(height - r + 1, width - c + 1) }
        DirectionScreen.Left -> transFunc = { r, c -> GridCellScreen(width - c + 1, r) }
    }

    for (column in columnProgression) {
        var emptyFields = 0
        for (row in rowProgression) {
            val curr = transFunc(row, column)
            if (cubeRocks.contains(curr)) {     // cube rock found
                emptyFields = 0
            } else if (this.contains(curr)) {   // round rock found
                val destination = curr.moveTimes(facing, emptyFields)
                result.add(destination)
            } else {                            // empty field found
                emptyFields++
            }
        }
    }
    return result
}

fun solve2(input: List<String>, cycles: Int = 0): Long {
    height = input.size
    width = input.first().length
    cubeRocks = Grid.of(input, GridCellScreen(1, 1), ignoreChars = listOf('.', 'O')).gridMap.keys.toSet()
    val round = Grid.of(
        input,
        GridCellScreen(1, 1),
        ignoreChars = listOf('.', '#'),
        relevantChars = listOf('O')
    ).gridMap.keys.toSet()
    var r = round
    if (cycles == 0) {
        r = r.compress(DirectionScreen.Up)
    } else {
        var absolvedCycles = 0
        var caching = true
        while (absolvedCycles < cycles) {
            val beforeHash = r.sortedWith(compareBy { it.toString() }).toString()
//            println("hash $beforeHash")
            var doneInRound: Int
            if (caching && dishCache.containsKey(beforeHash)) {
                doneInRound = dishCache.getValue(beforeHash)
                val loopLength = absolvedCycles - doneInRound + 1
                println("    after $absolvedCycles done in round $doneInRound loopLength $loopLength")
                absolvedCycles = cycles - ((cycles - absolvedCycles) % loopLength)
                println("  > fast forward to $absolvedCycles")
                caching = false
                continue
            }

            val after = r
                .compress(DirectionScreen.Up)
                .compress(DirectionScreen.Left)
                .compress(DirectionScreen.Down)
                .compress(DirectionScreen.Right)
            absolvedCycles++
            dishCache[beforeHash] = absolvedCycles
            r = after
        }
    }
    val rowProgression = IntProgression.fromClosedRange(1, height, 1)
    val columnProgression = IntProgression.fromClosedRange(1, width, 1)

//    printDish(round)
//    println()
    var res = 0L
    for (row in rowProgression) {
        var roundInRow = 0
        for (column: Int in columnProgression) {
            if (r.contains(GridCellScreen(row, column))) {
                roundInRow++
            }
        }
        res += (height - row + 1) * roundInRow
    }
//    printDish(r)
//    println()
    return res
}

fun main() {
    fun part1(input: List<String>): Long = solve2(input)

    fun part2(input: List<String>): Long = solve2(input, 1_000_000_000)

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 136L)
    executeAndCheck(1, 113_456L) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 64L)
    executeAndCheck(2, 118_747L) { // nicht 118_768
        part2(puzzleInput)
    }
}

fun printDish(round: Set<GridCellScreen>) {
    for (row in 1..height) {
        for (column in 1..width) {
            val curr = GridCellScreen(row, column)
            if (cubeRocks.contains(curr)) {
                print('#')
            } else if (round.contains(curr)) {
                print('O')
            } else {
                print('.')
            }
        }
        println()
    }
}
