/**
 * Advent of Code 2023, Day 08: Haunted Wasteland
 * Problem Description: https://adventofcode.com/2023/day/08
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.direction.DirectionScreen
import de.niemeyer.aoc.direction.toDirectionScreen
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName
import de.niemeyer.aoc.utils.lcm
import de.niemeyer.aoc.utils.repeatInfinite

fun main() {
    fun part1(input: List<String>): Long {
        val nodes = input.drop(2).associate { Node.of(it) }
        val directions = input.first().map { it.toDirectionScreen() }
        val result = directions.repeatInfinite().runningFold(START_NODE to 0L) { (current, steps), direction ->
            nodes.getValue(current).next(direction) to steps + 1
        }.first { it.first == TARGET_NODE }
        return result.second
    }

    fun part2(input: List<String>): Long {
        val directions = input.first().map { it.toDirectionScreen() }
        val nodes = input.drop(2).associate { Node.of(it) }
        val startNodes = nodes.keys.filter { it.endsWith("A") }
        val steps = MutableList(startNodes.size) { 0L }
        val startStates = startNodes.zip(steps)
        val result = directions.repeatInfinite().runningFold(startStates) { states, direction ->
            val newStates = states.map { (position, steps) ->
                if (position.endsWith("Z")) {
                    position to steps
                } else {
                    nodes.getValue(position).next(direction) to steps + 1
                }
            }
            newStates
        }.first { it.all { state -> state.first.endsWith("Z") } }

        return result.toList()
            .map { it.second }
            .reduce { acc, it ->
                acc.lcm(it)
            }
    }

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val testInput2 = resourceAsList(fileName = "${name}_test2.txt")
    val testInput3 = resourceAsList(fileName = "${name}_test3.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 2L)
    check(part1(testInput2) == 6L)
    executeAndCheck(1, 16_697L) {
        part1(puzzleInput)
    }

    check(part2(testInput3) == 6L)
    executeAndCheck(2, 10_668_805_667_831L) {
        part2(puzzleInput)
    }
}

const val START_NODE = "AAA"
const val TARGET_NODE = "ZZZ"

data class Node(val left: String, val right: String) {
    fun next(direction: DirectionScreen) =
        when (direction) {
            DirectionScreen.Left -> left
            else -> right
        }

    companion object {
        private val NODE = """(\w+)\s+=\s+\((\w+),\s+(\w+)\)""".toRegex()
        fun of(input: String): Pair<String, Node> =
            NODE.findAll(input).map { matchResult ->
                val (node, left, right) = matchResult.destructured
                node to Node(left, right)
            }.first()
    }
}

