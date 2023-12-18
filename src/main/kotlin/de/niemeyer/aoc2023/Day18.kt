/**
 * Advent of Code 2023, Day 18:
 * Problem Description: https://adventofcode.com/2023/day/18
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.direction.DirectionScreen
import de.niemeyer.aoc.direction.toDirectionScreen
import de.niemeyer.aoc.points.Point2D
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*

fun main() {
    fun solve(input: List<String>): Long {
        val instructions = input.map { Dig.of(it) }

        val plan = buildMap<Point2D, Pair<DirectionScreen, DirectionScreen>> {
            var current = Point2D.ORIGIN
            this[current] = DirectionScreen.Right to DirectionScreen.Right
            instructions.forEach { dig ->
                repeat(dig.distance) {
                    this[current] = this[current]!!.first to dig.direction
                    current = current.move(dig.direction)
                    this[current] = dig.direction to (this[current]?.second ?: dig.direction)
                }
            }
        }
        var interior = 0L
        val rowMin = plan.keys.minOf { it.y }
        val rowMax = plan.keys.maxOf { it.y }
        val columnMin = plan.keys.minOf { it.x }
        val columnMax = plan.keys.maxOf { it.x }
        val verticalDirs = listOf(DirectionScreen.Up, DirectionScreen.Down)
        for (row in rowMin..rowMax) {
            var inside = false
            var firstDig: Pair<DirectionScreen, DirectionScreen>? = null
            var lastDig: Pair<DirectionScreen, DirectionScreen>? = null
            for (column in columnMin..columnMax) {
                val point = Point2D(column, row)
                val dig = plan[point]
                if (lastDig == null && dig != null) {
                    firstDig = dig
                } else if (lastDig != null && dig == null) {
                    if (firstDig!!.first == lastDig.second && lastDig.second in verticalDirs
                    ) {
                        inside = !inside
                    } else if (firstDig!!.second == lastDig.first && lastDig.first in verticalDirs
                    ) {
                        inside = !inside
                    }
                }
                lastDig = dig
                if (dig != null || inside) {
                    print(if (dig != null) '#' else 'O')
                    interior++
                } else print('.')
            }
            println()
        }
        return interior
    }

    fun part1(input: List<String>): Long = solve(input)

    fun part2(input: List<String>): Long = TODO()

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 62L)
    executeAndCheck(1, 39_039L) {
        part1(puzzleInput)
    }

//    check(part2(testInput) == 952_408_144_115L)
//    executeAndCheck(2, 0L) {
//        part2(puzzleInput)
//    }
}

data class Dig(val direction: DirectionScreen, val distance: Int, val rgb: String) {
    companion object {
        fun of(input: String): Dig {
            val parts = input.split(' ')
            val direction = parts.first().toDirectionScreen()
            val distance = parts[1].toInt()
            val rgb = parts.last().substringAfter('(').substringBefore(')')
            return Dig(direction, distance, rgb)
        }
    }
}
