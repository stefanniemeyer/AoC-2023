/**
 * Advent of Code 2023, Day 13: Point of Incidence
 * Problem Description: https://adventofcode.com/2023/day/13
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsText
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName
import de.niemeyer.aoc.utils.rotateRight

fun main() {
    fun solve(input: String, smucks: Int = 0): Int =
        input
            .split("\n\n")
            .map { it.trim().lines() }
            .sumOf { pattern ->
                val horizontal = mirrored(pattern, smucks)
                val vertical = mirrored(pattern.rotateRight(), smucks)
                horizontal * 100 + vertical
            }

    fun part1(input: String): Int = solve(input)

    fun part2(input: String): Int = solve(input, smucks = 1)

    val name = getClassName()
    val testInput = resourceAsText(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsText(fileName = "${name}.txt")

    check(part1(testInput) == 405)
    executeAndCheck(1, 29_213) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 400)
    executeAndCheck(2, 37_453) {
        part2(puzzleInput)
    }
}

fun mirrored(pattern: List<String>, smucks: Int = 0): Int =
    (0..<pattern.lastIndex).firstNotNullOfOrNull { idx ->
        (0..idx).map { mirrorIdx ->
            idx - mirrorIdx to idx + mirrorIdx + 1
        }.filterNot { (_, down) ->
            (down > pattern.lastIndex)
        }.sumOf { (up, down) ->
            pattern[up]
                .zip(pattern[down])
                .count { (a, b) -> a != b }
        }.let { if (smucks == it) idx + 1 else null }
    } ?: 0
