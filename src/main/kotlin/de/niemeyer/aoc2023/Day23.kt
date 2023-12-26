/**
 * Advent of Code 2023, Day 23: A Long Walk
 * Problem Description: https://adventofcode.com/2023/day/23
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.direction.DirectionScreen
import de.niemeyer.aoc.direction.arrowToDirectionScreen
import de.niemeyer.aoc.direction.toArrow
import de.niemeyer.aoc.grid.GridCellScreen
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*

fun main() {
    fun part1(input: List<String>): Int =
        Day23(input).solve()

    fun part2(input: List<String>): Int =
        Day23(input).bfs2()

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 94)
    executeAndCheck(1, 2_442) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 154)
    executeAndCheck(2, 6_898) {
        part2(puzzleInput)
    }
}

class Day23() {
    lateinit var snowIsland: SnowIsland

    constructor(input: List<String>) : this() {
        snowIsland = SnowIsland.of(input)
    }

    fun solve(slopes: Boolean = true): Int {
        return bfs(slopes)
    }

    fun bfs2(): Int {
        val start = snowIsland.graph.keys.find { it.row == snowIsland.rowMin } ?: error("no start point found")
        val target = snowIsland.graph.keys.find { it.row == snowIsland.rowMax } ?: error("no end point found")
        val conjGraph = HashMap<GridCellScreen, HashSet<Pair<GridCellScreen, Int>>>()
        val visited = HashSet<GridCellScreen>()

        val conjunctions = snowIsland.graph.keys.map { pathElement ->
            pathElement to pathElement.axisNeighbors
                .filter { neighbour ->
                    neighbour in snowIsland.graph.keys
                }.size
        }.filter { it.second > 2 }
            .map { it.first }
        val queue = ArrayDeque<Triple<List<GridCellScreen>, GridCellScreen, Int>>()
        queue += Triple(listOf(start), start, 0)
        while (queue.isNotEmpty()) {
            val (path, conj, dist) = queue.removeFirst()
            val pos = path.last()
            val nextPos = pos.axisNeighbors
                .filter { it in snowIsland.graph.keys }
                .filterNot { it in path }
                .filter { it !in visited || it in conjunctions || it == target }
            val newDist = dist + 1
            val cands = nextPos.mapNotNull { np ->
                if (np in conjunctions || np == target) {
                    conjGraph.getOrPut(conj) { HashSet() } += np to newDist
                    conjGraph.getOrPut(np) { HashSet() } += conj to newDist

                    visited.addAll(path)
                    Triple(listOf(np), np, 0).takeIf { np != target }
                } else {
                    Triple(path + np, conj, newDist)
                }
            }
            queue.addAll(cands)
        }

        return longestPath(start, target, conjGraph)
    }

    fun longestPath(
        start: GridCellScreen,
        target: GridCellScreen,
        graph: HashMap<GridCellScreen, HashSet<Pair<GridCellScreen, Int>>>
    ): Int {
        val queue = ArrayDeque<IndexedValue<List<GridCellScreen>>>()
        var solution = 0
        queue += IndexedValue(0, listOf(start))
        while (queue.isNotEmpty()) {
            val (dist, path) = queue.removeFirst()
            val pos = path.last()
            val neighbours = graph[pos] ?: error("What happened?")
            val nextPos = neighbours.toList().filterNot { it.first in path }
            nextPos.forEach { (npos, ndist) ->
                if (npos == target && (dist + ndist > solution)) {
                    solution = dist + ndist
//                    println("TARGET: $solution")
                } else {
                    queue += IndexedValue(dist + ndist, path + npos)
                }
            }
        }

        return solution
    }

    fun bfs(slopes: Boolean): Int {
        val start = snowIsland.graph.keys.find { it.row == snowIsland.rowMin } ?: error("no start point found")
        val target = snowIsland.graph.keys.find { it.row == snowIsland.rowMax } ?: error("no end point found")
        val queue = ArrayDeque<IndexedValue<List<GridCellScreen>>>()
        var solution = 0
        queue.add(IndexedValue(0, listOf(start)))
        while (queue.isNotEmpty()) {
            val (steps, path) = queue.removeFirst()
            val pos = path.last()
            if (pos == target) {
                solution = maxOf(steps, solution)
                continue
            }

            val pathElement = snowIsland.graph.getValue(pos)
            val nextPosCand =
                if (pathElement == null || slopes == false) {
                    pos.axisNeighbors
                } else {
                    listOf(pos + pathElement.offset.toGridCellScreen())
                }
            val nextPos = nextPosCand
                .filter { it in snowIsland.graph.keys }
                .filterNot { it in path }
            val cands = nextPos.map { IndexedValue(steps + 1, path + it) }
            queue.addAll(cands)
        }
        return solution
    }
}

class SnowIsland(
    var graph: Map<GridCellScreen, DirectionScreen?>,
    val offset: GridCellScreen = GridCellScreen(1, 1)
) {
    val rowMin = if (graph.isEmpty()) 0 else graph.keys.minOf { it.row }
    val rowMax = if (graph.isEmpty()) 0 else graph.keys.maxOf { it.row }
    val columnMin = if (graph.isEmpty()) 0 else graph.keys.minOf { it.column }
    val columnMax = if (graph.isEmpty()) 0 else graph.keys.maxOf { it.column }

    @Suppress("unused")
    fun printExisting() {
        for (row in rowMin..rowMax) {
            for (column in columnMin..columnMax) {
                val cell = GridCellScreen(row, column)
                if (graph.containsKey(cell)) {
                    val path = graph.get(cell)
                    if (path == null) printColored(".", AnsiColor.BLUE)
                    else printColored(path.toArrow(), AnsiColor.RED)
                } else print('#')
            }
            println()
        }
    }

    companion object {
        fun of(input: List<String>, offset: GridCellScreen = GridCellScreen(1, 1)): SnowIsland =
            input.mapIndexed { rowIndex, row ->
                row.mapIndexedNotNull { columnIndex, c ->
                    if (c == '#') {
                        null
                    } else {
                        val cell = GridCellScreen(rowIndex + offset.row, columnIndex + offset.column)
                        when (c) {
                            '.' -> cell to null
                            else -> cell to c.arrowToDirectionScreen()
                        }
                    }
                }
            }.flatten().toMap().let { SnowIsland(it, offset) }
    }
}
