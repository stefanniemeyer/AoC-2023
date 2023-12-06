/**
 * Advent of Code 2023, Day 1: Trebuchet?!
 * Problem Description: https://adventofcode.com/2023/day/1
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName

fun main() {
    fun addFirstAndLast(input: List<String>) =
        input.map {
            it.filter { letter -> letter.isDigit() }
        }.map { nums ->
            "${nums.first()}${nums.last()}".toInt()
        }.sum()

    fun part1(input: List<String>): Int =
        addFirstAndLast(input)

    fun part2(input: List<String>): Int {
        val newInput = NumConverter.parse(input)
        val res = addFirstAndLast(newInput)
        return res
    }

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

data class NumConverter(val nums: List<String>) {
    companion object {
        val numberAsString = mapOf(
            "one" to "1",
            "two" to "2",
            "three" to "3",
            "four" to "4",
            "five" to "5",
            "six" to "6",
            "seven" to "7",
            "eight" to "8",
            "nine" to "9"
        )
        val reverserdNumberAsString = numberAsString.mapKeys { (k, _) -> k.reversed() }

        fun replaceSpelledNumbers(text: String, numbers: Map<String, String>): String {
            if (text.isEmpty()) return ""
            val rf = replaceSpelledNumbersAtStart(numbers, text)
            val f = rf.first()
            if (f.isDigit()) return f.toString()
            return replaceSpelledNumbers(rf.substring(1), numbers)
        }

        fun replaceSpelledNumbersAtStart(numbers: Map<String, String>, text: String): String {
            var res = text
            numbers.forEach { k, v ->
                if (text.startsWith(k)) {
                    res = text.replaceFirst(k, v)
                }
            }
            return res
        }

        fun parse(input: List<String>): List<String> =
            input.map { line ->
                val res = replaceSpelledNumbers(line, numberAsString).filter { it.isDigit() }.first().toString()
                val revRes = replaceSpelledNumbers(line.reversed(), reverserdNumberAsString).filter { it.isDigit() }.first().toString()
                res + revRes
            }
    }
}
