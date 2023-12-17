/**
 * Advent of Code 2023, Day 17:
 * Problem Description: https://adventofcode.com/2023/day/17
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsArrayOfIntArray
import de.niemeyer.aoc.direction.DirectionScreen
import de.niemeyer.aoc.points.Point2D
import de.niemeyer.aoc.utils.*
import java.util.PriorityQueue

fun searchPath(
    grid: Array<IntArray>,
    nextSteps: (DirectionScreen, Int) -> List<DirectionScreen>,
    validateDistance: (Int) -> Boolean
): Int {
    val visited = mutableSetOf<State>()
    val queue = PriorityQueue<IndexedValue<State>>(compareBy { (cost, state) ->
        cost - Point2D.ORIGIN.manhattanDistanceTo(state.position)
    })
    val target = Point2D(grid.last().lastIndex, grid.lastIndex)
    queue.add(IndexedValue(0, State(Point2D.ORIGIN, DirectionScreen.Right, 0)))
    while (!queue.isEmpty()) {
        val (cost, state) = queue.remove()
        if (state.position == target && validateDistance(state.distance)) return cost
        if (!visited.add(state)) continue
        nextSteps(state.direction, state.distance).forEach { direction ->
            val stateCand = state.move(direction)
//            if (stateCand.position.x in xRanges && stateCand.position.y in yRanges)
            if (stateCand.position in grid)
                queue.add(IndexedValue(cost + grid[stateCand.position.y][stateCand.position.x], stateCand))
        }
    }

    return Int.MAX_VALUE
}

fun main() {
    fun part1(input: Array<IntArray>): Int =
        searchPath(input, ::nextStepsNormal, validateDistance = { true })

    fun part2(input: Array<IntArray>): Int =
        searchPath(input, ::nextStepsUltra, validateDistance = { it >= 4 }).also { println("res=$it") }

    val name = getClassName()
    val testInput = resourceAsArrayOfIntArray(fileName = "${name}_test.txt")
    val testInput2 = resourceAsArrayOfIntArray(fileName = "${name}_test2.txt")
    val puzzleInput = resourceAsArrayOfIntArray(fileName = "${name}.txt")

    check(part1(testInput) == 102)
    executeAndCheck(1, 963) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 94)
    check(part2(testInput2) == 71)
    executeAndCheck(2, 0) { // 1186 to high
        part2(puzzleInput)
    }
}

fun nextStepsNormal(direction: DirectionScreen, distance: Int): List<DirectionScreen> =
    when {
        (distance < 3) -> listOf(direction, direction.turnLeft, direction.turnRight)
        else -> listOf(direction.turnLeft, direction.turnRight)
    }

fun nextStepsUltra(direction: DirectionScreen, distance: Int): List<DirectionScreen> =
    when {
        (distance < 4) -> listOf(direction)
        (distance < 10) -> listOf(direction, direction.turnLeft, direction.turnRight)
        else -> listOf(direction.turnLeft, direction.turnRight)
    }

data class State(val position: Point2D, val direction: DirectionScreen, val distance: Int)

fun State.move(heading: DirectionScreen): State {
    val newPos = position.move(heading)
    val newDistance = if (direction == heading) distance + 1 else 1
    return State(newPos, heading, newDistance)
}
