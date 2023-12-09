/**
 * Advent of Code 2023, Day 09: Mirage Maintenance
 * Problem Description: https://adventofcode.com/2023/day/09
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsListOfListOfLong
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName

fun main() {
    fun diffToNext(nums: List<Long>) =
        nums
            .windowed(2, 1)
            .map { it.last() - it.first() }

    fun recursiveDiff(list: List<Long>, acc: MutableList<List<Long>> = mutableListOf()): List<List<Long>> {
        acc.add(list)
        val nextList = diffToNext(list)
        return if (nextList.all { it == 0L }) acc else recursiveDiff(nextList, acc)
    }

    fun part1(input: List<List<Long>>): Long =
        input.map { nums ->
            recursiveDiff(nums).map { it.last() }
                .reversed()
                .fold(0L) { acc, num ->
                    acc + num
                }
        }.sum()

    fun part2(input: List<List<Long>>): Long =
        input.map { nums ->
            recursiveDiff(nums)
                .map { it.first() }
                .reversed()
                .fold(0L) { acc, num ->
                    num - acc
                }
        }.sum()

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
