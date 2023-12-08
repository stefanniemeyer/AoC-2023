/**
 * Advent of Code 2023, Day 1: Trebuchet?!
 * Problem Description: https://adventofcode.com/2023/day/1
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName

fun main() {
    val digits = List(10) { IndexedValue(it, it.toString()) }
    val words = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        .mapIndexed { idx, word ->
            IndexedValue(idx + 1, word)
        }
    val extendedDigits = digits + words

    fun addFirstAndLast(input: List<String>, nums: List<IndexedValue<String>>): Int =
        input.map { line ->
            line.windowed(5, 1, partialWindows = true)  // 5 == max len of elements in 'words'
                .mapNotNull { part ->
                    nums.firstOrNull { part.startsWith(it.value) }?.index
                }
        }.map { digitsFound ->
            10 * digitsFound.first() + digitsFound.last()
        }.sum()

    fun part1(input: List<String>): Int =
        addFirstAndLast(input, digits)

    fun part2(input: List<String>): Int =
        addFirstAndLast(input, extendedDigits)

    fun readInput(fileName: String): List<String> =
        resourceAsList(fileName)

    val name = getClassName()
    val testInput = readInput(fileName = "${name}_test.txt")
    val testInput2 = readInput(fileName = "${name}_test2.txt")
    val puzzleInput = readInput(fileName = "${name}.txt")

    check(part1(testInput) == 142)
    executeAndCheck(1, 54_601) {
        part1(puzzleInput)
    }

    check(part2(testInput2) == 281)
    executeAndCheck(2, 54_078) {
        part2(puzzleInput)
    }
}
