/**
 * Advent of Code 2023, Day 12:
 * Problem Description: https://adventofcode.com/2023/day/12
 * Disclaimer: The solution is basically from Kroppeb/AdventOfCodeSolutions2. I did a bit refactoring.
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName

fun main() {
    fun part1(input: List<String>): Long =
        input.map { line ->
            val records = line.substringBefore(" ")
            val groups = line.substringAfter(" ").split(",").map(String::toInt)
            findOrder(groups, records)
        }.sum()

    fun part2(input: List<String>): Long =
        input.map { line ->
            val records = line.substringBefore(" ")
            val groups = line.substringAfter(" ").split(",").map(String::toInt)
            val records5 = List(5) { records }.joinToString("?")
            val groups5 = List(5) { groups }.flatten()
            findOrder(groups5, records5)
        }.sum()

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 21L)
    executeAndCheck(1, 7_857L) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 525_152L)
    executeAndCheck(2, 28_606_137_449_920L) {
        part2(puzzleInput)
    }
}

val cache = mutableMapOf<Pair<List<Int>, String>, Long>()

fun findOrder(groups: List<Int>, record: String): Long {
    if (record.isEmpty()) {                     // record is empty. That is ...
        return if (groups.isEmpty()) 1 else 0   // OK, if groups are empty as well, and NOK otherwise
    }

    val restRecord = record.drop(1)

    return when (record.first()) {
        '.' -> findOrder(groups, restRecord)
        '#' -> getOrder(groups, record).also { cache[groups to record] = it }
        '?' -> findOrder(groups, restRecord) + getOrder(groups, record).also { cache[groups to record] = it }
        // findOrder(groups, restRecord) act as if '?' was a '.'
        // getOrder(groups, record)      act as if '?' was a '#'
        else -> error("Unexpected Character '${record.first()}'")
    }
}

private fun getOrder(groups: List<Int>, record: String): Long {
    cache[groups to record]?.let { return it }                    // solution is cached

    if (groups.isEmpty()) return 0                                // all groups are processed
    val firstGroup = groups.first()

    if (record.length < firstGroup) return 0                      // fewer chars in the record than needed for the group
    if (record.indexOf('.') in 0..<firstGroup) return 0   // not enough chars in the record before the next '.'
    if (record.length == firstGroup) {
        return if (groups.size == 1) 1 else 0                     // only 1 group left and record has exactly group size
    }
    if (record[firstGroup] == '#') return 0                       // first char after group cannot be '#'

    return findOrder(groups.drop(1), record.drop(firstGroup + 1))
}
