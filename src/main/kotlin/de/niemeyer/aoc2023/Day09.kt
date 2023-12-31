/**
 * Advent of Code 2023, Day 09: Mirage Maintenance
 * Problem Description: https://adventofcode.com/2023/day/09
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsListOfListOfLong
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName
import de.niemeyer.aoc.utils.recursiveDiff

fun solve(input: List<List<Long>>): Long =
    input.sumOf { nums ->
        recursiveDiff(nums).map { it.last() }
            .reversed()
            .fold(0L) { acc, num ->
                num + acc
            }
    }

fun main() {
    fun part1(input: List<List<Long>>): Long =
        solve(input)

    fun part2(input: List<List<Long>>): Long =
        solve(input.map { it.reversed() })

    val name = getClassName()
    val testInput = resourceAsListOfListOfLong(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsListOfListOfLong(fileName = "${name}.txt")

    check(part1(testInput) == 114L)
    executeAndCheck(1, 1_798_691_765L) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 2L)
    executeAndCheck(2, 1_104L) {
        part2(puzzleInput)
    }
}
