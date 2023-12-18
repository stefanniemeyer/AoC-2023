/**
 * Advent of Code 2023, Day 17: Clumsy Crucible
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
    val target = Point2D(grid.last().lastIndex, grid.lastIndex)
    val visited = mutableSetOf<State>()
    val queue = PriorityQueue<IndexedValue<State>>(compareBy { (cost, state) ->
        cost // - Point2D.ORIGIN.manhattanDistanceTo(state.position)
    })
    queue.add(IndexedValue(0, State(Point2D.ORIGIN, DirectionScreen.Right, 0)))
    while (queue.isNotEmpty()) {
        val (cost, state) = queue.poll()
//        println("pos: ${state.position} -> ${state.direction}   dist: ${state.distance}")
        if (state.position == target) {
            if (validateDistance(state.distance)) {
//                println("FINAL pos: ${state.position} dir: ${state.direction}  distance ${state.distance}")
                return cost
            }
//            println("INVALID distance in target pos: ${state.position} distance ${state.distance}")
        }
        if (visited.add(state)) {
            nextSteps(state.direction, state.distance).forEach { direction ->
                val stateCand = state.move(direction)
                if (stateCand.position in grid) {
//                    println("   cand: ${stateCand.position} -> ${stateCand.direction}   dist: ${stateCand.distance}")
                    queue.add(IndexedValue(cost + grid[stateCand.position.y][stateCand.position.x], stateCand))
                }
            }
        }
    }

    error("No solution found")
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
    executeAndCheck(2, 1178) { // 1186 to high
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
