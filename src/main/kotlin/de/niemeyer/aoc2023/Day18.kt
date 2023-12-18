/**
 * Advent of Code 2023, Day 18: Lavaduct Lagoon
 * Problem Description: https://adventofcode.com/2023/day/18
 * Disclaimer: The solution is from Michael de Kaste w/ minimal refactoring from my side.
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.direction.DirectionScreen
import de.niemeyer.aoc.direction.toDirectionScreen
import de.niemeyer.aoc.points.Point2D
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*
import kotlin.math.absoluteValue

fun List<Pair<DirectionScreen, Int>>.solve() =
    runningFold(Point2D.ORIGIN) { point, (dir, amount) ->
        point.moveTimes(dir, amount)
    }.zipWithNext { p1, p2 ->
        (p2.x - p1.x) * p1.y.toLong()
    }.sum().absoluteValue + sumOf { it.second } / 2 + 1

private fun String.toDigLine(): Pair<DirectionScreen, Int> {
    val dir = listOf(DirectionScreen.Right, DirectionScreen.Down, DirectionScreen.Left, DirectionScreen.Up)[get(lastIndex - 1).digitToInt()]
    return dir to this.substring(2..6).toInt(16)
}

fun main() {
    fun part1(input: List<String>): Long =
        input.map { it.split(' ') }.map { (a, b, _) -> a[0].toDirectionScreen() to b.toInt() }.solve()

    fun part2(input: List<String>): Long = input.map { it.split(' ') }.map { (_, _, c) -> c.toDigLine() }.solve()

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 62L)
    executeAndCheck(1, 39_039L) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 952_408_144_115L)
    executeAndCheck(2, 44_644_464_596_918L) {
        part2(puzzleInput)
    }
}
