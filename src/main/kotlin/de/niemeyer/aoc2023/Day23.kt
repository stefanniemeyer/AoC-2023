/**
 * Advent of Code 2023, Day 23: A Long Walk
 * Problem Description: https://adventofcode.com/2023/day/23
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.direction.DirectionScreen
import de.niemeyer.aoc.direction.arrowToDirectionScreen
import de.niemeyer.aoc.direction.toArrow
import de.niemeyer.aoc.grid.Grid
import de.niemeyer.aoc.grid.GridCellContainer
import de.niemeyer.aoc.grid.GridCellScreen
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*
import java.util.PriorityQueue

fun main() {
    fun part1(input: List<String>): Int =
        Day23(input).solve()

    fun part2(input: List<String>): Int =
        TODO()

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 94)
    executeAndCheck(1, 2_442) {
        part1(puzzleInput)
    }

//    check(part2(testInput) == 0)
//    executeAndCheck(2, 0) {
//        part2(puzzleInput)
//    }
}

class Day23() {
    lateinit var snowIsland: SnowIsland

    constructor(input: List<String>) : this() {
        snowIsland = SnowIsland.of(input)
    }

    fun solve(): Int {
        return dfs()
    }

    fun dfs(): Int {
        val start = snowIsland.gridMap.keys.find { it.row == snowIsland.rowMin } ?: error("no start point found")
        val target = snowIsland.gridMap.keys.find { it.row == snowIsland.rowMax } ?: error("no end point found")
        val queue = PriorityQueue<IndexedValue<List<GridCellScreen>>>(compareBy { -it.index })
        var solution = 0
        queue.add(IndexedValue(0, listOf(start)))
        while (queue.isNotEmpty()) {
            val (steps, path) = queue.poll()
            val pos = path.last()
            if (pos == target) {
                solution = maxOf(steps, solution)
                continue
            }

            val pathElement = snowIsland.gridMap.getValue(pos)
            val nextPosCand =
                if (pathElement == null) {
                    pos.axisNeighbors
                } else {
                    listOf(pos + pathElement.offset.toGridCellScreen())
                }
            val nextPos = nextPosCand
                .filter { it in snowIsland.gridMap.keys }
                .filterNot { it in path }
            val cands = nextPos.map { IndexedValue(steps + 1, path + it) }
            queue.addAll(cands)
        }
        return solution
    }
}

class SnowIsland(
    var gridMap: Map<GridCellScreen, DirectionScreen?>,
    val offset: GridCellScreen = GridCellScreen(1, 1)
) {
    val rowMin = if (gridMap.isEmpty()) 0 else gridMap.keys.minOf { it.row }
    val rowMax = if (gridMap.isEmpty()) 0 else gridMap.keys.maxOf { it.row }
    val columnMin = if (gridMap.isEmpty()) 0 else gridMap.keys.minOf { it.column }
    val columnMax = if (gridMap.isEmpty()) 0 else gridMap.keys.maxOf { it.column }

    val columnRangesForRows by lazy {
        val rowRange = gridMap.keys.minOf { it.row }..gridMap.keys.maxOf { it.row }
        rowRange.associateWith { row ->
            gridMap.keys.filter { it.row == row }.minOf { it.column }..gridMap.keys.filter { it.row == row }
                .maxOf { it.column }
        }
    }

    val rowRangesForColumns by lazy {
        val colRange = gridMap.keys.minOf { it.column }..gridMap.keys.maxOf { it.column }
        colRange.associateWith { col ->
            gridMap.keys.filter { it.column == col }.minOf { it.row }..gridMap.keys.filter { it.column == col }
                .maxOf { it.row }
        }
    }

    fun printExisting() {
        for (row in rowMin..rowMax) {
            for (column in columnMin..columnMax) {
                val cell = GridCellScreen(row, column)
                if (gridMap.containsKey(cell)) {
                    val path = gridMap.get(cell)
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
