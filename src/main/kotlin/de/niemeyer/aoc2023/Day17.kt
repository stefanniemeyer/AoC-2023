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
    minStepsAfterTurn: Int,
    maxStepsBeforeTurn: Int,
): Int {
    val target = Point2D(grid.last().lastIndex, grid.lastIndex)
    val visited = mutableSetOf<State>()
    val queue = PriorityQueue<IndexedValue<State>>(compareBy { it.index })
    queue.add(IndexedValue(0, State(Point2D.ORIGIN, DirectionScreen.Right, 0)))
    while (queue.isNotEmpty()) {
        val (cost, state) = queue.poll()
        if (state.position == target) {
            return cost
        }
        if (!visited.add(state)) {
            continue
        }
        if (state.distance < maxStepsBeforeTurn) {
            val stateCand = state.move(state.direction)
            if (stateCand.position in grid) {
                queue.add(IndexedValue(cost + grid[stateCand.position.y][stateCand.position.x], stateCand))
            }
        }

        fun addForTurn(direction: DirectionScreen) {
            var stateCand = state
            var newCost = cost
            repeat(minStepsAfterTurn) {
                stateCand = stateCand.move(direction)
                if (stateCand.position !in grid) {
                    // we can not move the number of required steps
                    return
                }
                newCost += grid[stateCand.position.y][stateCand.position.x]
            }
            queue.add(IndexedValue(newCost, State(stateCand.position, direction, minStepsAfterTurn)))
        }
        addForTurn(state.direction.turnRight)
        addForTurn(state.direction.turnLeft)
    }

    error("No solution found")
}

fun main() {
    fun part1(input: Array<IntArray>): Int = searchPath(input, 1, 3)

    fun part2(input: Array<IntArray>): Int =
        searchPath(input, 4, 10)

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
    executeAndCheck(2, 1_178) {
        part2(puzzleInput)
    }
}

data class State(val position: Point2D, val direction: DirectionScreen, val distance: Int)

fun State.move(heading: DirectionScreen): State {
    val newPos = position.move(heading)
    val newDistance = if (direction == heading) distance + 1 else 1
    return State(newPos, heading, newDistance)
}
