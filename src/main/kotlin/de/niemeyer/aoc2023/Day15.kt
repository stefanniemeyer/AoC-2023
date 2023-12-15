/**
 * Advent of Code 2023, Day 15: Lens Library
 * Problem Description: https://adventofcode.com/2023/day/15
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsText
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName

fun solve(input: String): Int =
    input.fold(0) { acc, c ->
        (acc + c.code) * 17 % 256
    }

fun main() {
    fun part1(input: String): Int =
        input.trim().split(',').sumOf { solve(it) }

    fun part2(input: String): Int =
        TODO()

    val name = getClassName()
    val testInput = resourceAsText(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsText(fileName = "${name}.txt")

    check(part1(testInput) == 1_320)
    executeAndCheck(1, 510_273) {
        part1(puzzleInput)
    }

//    check(part2(testInput) == 0)
//    executeAndCheck(2, 0) {
//        part2(puzzleInput)
//    }
}
