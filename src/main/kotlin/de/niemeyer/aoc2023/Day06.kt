/**
 * Advent of Code 2023, Day 06: Wait For It
 * Problem Description: https://adventofcode.com/2023/day/06
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName
import de.niemeyer.aoc.utils.product

val NUMBER = """\d+""".toRegex()

fun main() {
    fun part1(input: List<String>): Int =
        input.map { line ->
            NUMBER.findAll(line).map { it.value.toLong() }
        }.let { (times, durations) ->
            times.zip(durations)
        }.map { (time, duration) ->
            (1..<time).map { d ->
                d * (time - d)
            }.count { it > duration }
        }.toList().product()

    fun part2(input: List<String>): Int {
        val (time, duration) = input.map { line ->
            line.filter(Char::isDigit).toLong()
        }.let { it.first() to it.last() }

        return (1..<time).map { d ->
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
