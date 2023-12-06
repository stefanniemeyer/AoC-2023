/**
 * Advent of Code 2023, Day 06:
 * Problem Description: https://adventofcode.com/2023/day/06
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.getClassName

fun main() {
    fun part1(input: List<String>): Long {
        val x = input.map { line ->
            line.split(" ").drop(1).mapNotNull { it.toLongOrNull() }
        }
        val races = x.first().zip(x.last())
        val s = races.map { race ->
            (0..race.first).map { duration ->
                duration * (race.first - duration)
            }.filter { solution ->
                solution > race.second
            }.count()
        }.fold(1L) { acc, i ->
            acc * i
        }
        return s
    }

    fun part2(input: List<String>): Long {
        val x = input.map { line ->
            line.substringAfter(":").replace(" ", "").toLong()
        }
        val race = x.first() to x.last()
        val s = (0L..race.first).map { duration ->
            duration * (race.first - duration)
        }.filter { solution ->
            solution > race.second
        }.count().toLong()

        return s
    }

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 288L)
    val puzzleResultPart1 = part1(puzzleInput)
    println(puzzleResultPart1)
    check(puzzleResultPart1 == 1_159_152L)

    check(part2(testInput) == 71_503L)
    val puzzleResultPart2 = part2(puzzleInput)
    println(puzzleResultPart2)
    check(puzzleResultPart2 == 41_513_103L)
}
