/**
 * Advent of Code 2023, Day 06: Wait For It
 * Problem Description: https://adventofcode.com/2023/day/06
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName
import de.niemeyer.aoc.utils.product

fun main() {
    fun part1(input: List<String>): Int =
        input.map { line ->
            line.split(" ").drop(1).filter { it.isNotBlank() }.map { it.toInt() }
        }.let { (times, durations) ->
            times.zip(durations)
        }.map { (time, duration) ->
            (1 until time).map { d ->
                d * (time - d)
            }.count { it > duration }
        }.product()

    fun part2(input: List<String>): Int {
        val (time, duration) = input.map { line ->
            line.substringAfter(":").replace(" ", "").toLong()
        }.let { it.first() to it.last() }

        return (1 until time).map { d ->
            d * (time - d)
        }.count { it > duration }
    }

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 288)
    executeAndCheck(1, 1_159_152) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 71_503)
    executeAndCheck(2, 41_513_103) {
        part2(puzzleInput)
    }
}
