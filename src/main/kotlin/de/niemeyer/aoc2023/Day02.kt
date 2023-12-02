/**
 * Advent of Code 2023, Day 02: Cube Conundrum
 * Problem Description: https://adventofcode.com/2023/day/02
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.getClassName
import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        val (maxRed, maxGreen, maxBlue) = listOf(12, 13, 14)
        val games = input.map { Game.ofString(it) }
        val possible = games.filter { game ->
            game.cubes.all { cube ->
                cube.red <= maxRed && cube.green <= maxGreen && cube.blue <= maxBlue
            }
        }
        return possible.sumOf { it.id }
    }

    fun part2(input: List<String>): Int =
        input
            .map { Game.ofString(it) }
            .sumOf { it.power }

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 8)
    val puzzleResultPart1 = part1(puzzleInput)
    println(puzzleResultPart1)
    check(puzzleResultPart1 == 2_406)

    check(part2(testInput) == 2_286)
    val puzzleResultPart2 = part2(puzzleInput)
    println(puzzleResultPart2)
    check(puzzleResultPart2 == 78_375)
}

enum class COLOR {
    RED,
    GREEN,
    BLUE
}

data class Game(val id: Int, val cubes: List<Cube>) {
    val maxRed: Int
        get() = cubes.maxOf { it.red }

    val maxGreen: Int
        get() = cubes.maxOf { it.green }

    val maxBlue: Int
        get() = cubes.maxOf { it.blue }
    val power: Int
        get() = maxRed * maxGreen * maxBlue

    companion object {
        fun ofString(input: String): Game {
            val id = input.substringBefore(":").substringAfter(" ").toInt()
            val bags = input.substringAfter(":")
            val cubes = bags.split(";").map { Cube.ofString(it) }

            return Game(id, cubes)
        }
    }
}

data class Cube(val red: Int, val green: Int, val blue: Int) {
    companion object {
        fun ofString(input: String): Cube {
            val pattern = """(\d+)\s*(red|green|blue)""".toRegex()
            val colors = pattern.findAll(input).map { matchResult ->
                val (number, color) = matchResult.destructured
                enumValueOf<COLOR>(color.uppercase()) to number.toInt()
            }.toMap()
            return Cube(colors[COLOR.RED] ?: 0, colors[COLOR.GREEN] ?: 0, colors[COLOR.BLUE] ?: 0)
        }
    }
}
