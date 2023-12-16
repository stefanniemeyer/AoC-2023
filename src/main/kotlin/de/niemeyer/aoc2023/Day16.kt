/**
 * Advent of Code 2023, Day 16: The Floor Will Be Lava
 * Problem Description: https://adventofcode.com/2023/day/16
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.direction.DirectionScreen
import de.niemeyer.aoc.points.Point2D
import de.niemeyer.aoc.utils.Resources.resourceAsArrayOfCharArray
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

typealias Beam = Pair<Point2D, DirectionScreen>

fun solve(contraption: Contraption, start: Beam): Int {
    val queue = ArrayDeque<Pair<Point2D, DirectionScreen>>()
    val cache = mutableSetOf<Beam>()

    queue.addFirst(start)
    while (queue.isNotEmpty()) {
        val beam = queue.removeFirst()
        val newBeams = contraption.moveBeam(beam)
        newBeams.forEach { b ->
            if (b !in cache) {
                cache.add(b)
                queue += b
            }
        }
    }
    val energized = cache.map { it.first }.toSet()
    return energized.size
}

fun main() {
    fun part1(contraption: Contraption): Int {
        val start = Point2D(contraption.xMin - 1, contraption.yMin)
        return solve(contraption, start to DirectionScreen.Right)
    }

    fun part2(contraption: Contraption): Int {
        val topBeams = contraption.xRanges.map { Point2D(it, contraption.yMin - 1) to DirectionScreen.Down}
        val lowerBeams = contraption.xRanges.map { Point2D(it, contraption.yMax + 1) to DirectionScreen.Up}
        val leftBeams = contraption.yRanges.map { Point2D(contraption.xMax - 1, it) to DirectionScreen.Right}
        val rightBeams = contraption.yRanges.map { Point2D(contraption.xMax + 1, it) to DirectionScreen.Left}
        val best: Int
        runBlocking(Dispatchers.Default) {
            val all = listOf(topBeams, lowerBeams, leftBeams, rightBeams).flatten().map { start ->
                async {
                    solve(contraption, start)
                }
            }.awaitAll()
            best = all.max()
        }
        return best
    }

    val name = getClassName()
    val testInput = resourceAsArrayOfCharArray(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsArrayOfCharArray(fileName = "${name}.txt")

    check(part1(Contraption.of(testInput)) == 46)
    executeAndCheck(1, 8_901) {
        part1(Contraption.of(puzzleInput))
    }

    check(part2(Contraption.of(testInput)) == 51)
    executeAndCheck(2, 9_064) {
        part2(Contraption.of(puzzleInput))
    }
}

fun DirectionScreen.reflect(field: Field): DirectionScreen =
    when (this) {
        DirectionScreen.Down -> when (field) {
            Field.SLASH -> DirectionScreen.Left
            Field.BACKSLASH -> DirectionScreen.Right
            else -> this
        }

        DirectionScreen.Left -> when (field) {
            Field.SLASH -> DirectionScreen.Down
            Field.BACKSLASH -> DirectionScreen.Up
            else -> this
        }

        DirectionScreen.Right -> when (field) {
            Field.SLASH -> DirectionScreen.Up
            Field.BACKSLASH -> DirectionScreen.Down
            else -> this
        }

        DirectionScreen.Up -> when (field) {
            Field.SLASH -> DirectionScreen.Right
            Field.BACKSLASH -> DirectionScreen.Left
            else -> this
        }
    }

enum class Field(val value: Char) {
    EMPTY('.'),
    SLASH('/'),
    BACKSLASH('\\'),
    HORIZONTAL('-'),
    VERTICAL('|')
}

fun Char.toField(): Field =
    when (this) {
        '.' -> Field.EMPTY
        '/' -> Field.SLASH
        '\\' -> Field.BACKSLASH
        '-' -> Field.HORIZONTAL
        '|' -> Field.VERTICAL
        else -> error("Conversion of '$this' to Field is not supported")
    }

data class Contraption(
    val cavern: List<List<Field>>
) {
    val yMin = 0
    val yMax = cavern.size - 1
    val xMin = 0
    val xMax = cavern[0].size - 1
    val yRanges = yMin..yMax
    val xRanges = xMin..xMax

    companion object {
        fun of(input: Array<CharArray>): Contraption {
            val fields = input.map { row ->
                row.map { it.toField() }
            }
            return Contraption(fields)
        }
    }

    fun Point2D.inside() =
        x in xRanges && y in yRanges

    fun moveBeam(beam: Beam): List<Beam> {
        val (pos, dir) = beam
        val newPos = pos + dir.offset

        if (!newPos.inside()) return emptyList()
        val field = cavern[newPos.y][newPos.x]
        return when (field) {
            Field.EMPTY -> listOf(newPos to dir)

            Field.SLASH,
            Field.BACKSLASH -> listOf(newPos to dir.reflect(field))

            Field.HORIZONTAL -> when (dir) {
                DirectionScreen.Down,
                DirectionScreen.Up -> listOf(newPos to dir.turnLeft, newPos to dir.turnRight)

                DirectionScreen.Left,
                DirectionScreen.Right -> listOf(newPos to dir)
            }

            Field.VERTICAL -> when (dir) {
                DirectionScreen.Down,
                DirectionScreen.Up -> listOf(newPos to dir)

                DirectionScreen.Left,
                DirectionScreen.Right -> listOf(newPos to dir.turnLeft, newPos to dir.turnRight)
            }
        }
    }
}
