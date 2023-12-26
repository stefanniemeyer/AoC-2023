/**
 * Advent of Code 2023, Day 10: Pipe Maze
 * Problem Description: https://adventofcode.com/2023/day/10
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName
import de.niemeyer.aoc.grid.GridCellScreen
import de.niemeyer.aoc.direction.CompassDirectionScreen
import de.niemeyer.aoc.points.Point2D

fun dfs(grid: Map<GridCellScreen, PipeContainer>, initial: PipeContainer): Set<GridCellScreen> {
    val visited = mutableSetOf<GridCellScreen>()
    val queue = ArrayDeque<GridCellScreen>()
    queue += initial.original
    while (queue.isNotEmpty()) {
        val cell = queue.removeFirst()
        visited += cell
        val cellPipe = grid.getValue(cell)
        val allowed = cellPipe.value.connectionOffsets
        val allowedReal = allowed.map { cell + it }.filterNot { it in visited }.mapNotNull { grid[it] }
        val connected = allowedReal.filter { cellPipe.isConnected(it) }.map { it.original }
        queue += connected
    }
    return visited
}

fun ignoreStartPipe(start: PipeContainer, grid: PipeGrid): Boolean {
    val x = start.value.connectionOffsets.filter { conn ->
        grid.gridMap[start.original + conn]?.let { neighbour ->
            neighbour.value.connectionOffsets.any { neighbour.original + it == start.original }
        } ?: false
    }
    return x.none { it.row == 1 }
}

fun main() {
    fun solve(input: List<String>, areaEnclosed: Boolean = false): Int {
        val grid = PipeGrid.of(input)
        val start = grid.gridMap.values.first { it.value == Pipe.Start }
        val loopPath = dfs(grid.gridMap, start)
        val loopGrid = PipeGrid(grid.gridMap.filter { it.key in loopPath })

        if (!areaEnclosed) {
            return loopGrid.gridMap.size / 2
        }

        println(loopGrid.toPrintableStringWithDefault())
        val ignoreTiles = mutableListOf(Pipe.NorthWest, Pipe.NorthEast, Pipe.Horizontal)
        if (ignoreStartPipe(start, loopGrid)) {
            ignoreTiles.add(Pipe.Start)
        }
        var insideCount = 0
        var isInside = false
        (loopGrid.rowMin..loopGrid.rowMax).forEach { row ->
            (loopGrid.columnMin..loopGrid.columnMax).forEach { column ->
                val cell = loopGrid.gridMap[GridCellScreen(row, column)]
                if (cell == null) {
                    if (isInside) {
                        insideCount++
                    }
                } else if (cell.value !in ignoreTiles) {
                    isInside = !isInside
                }
            }
        }

        return insideCount
    }

    fun part1(input: List<String>): Int =
        solve(input)

    fun part2(input: List<String>): Int =
        solve(input, areaEnclosed = true)

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val testInput1 = resourceAsList(fileName = "${name}_test1.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 8)
    executeAndCheck(1, 7_086) {
        part1(puzzleInput)
    }

    check(part2(testInput1) == 8)
    executeAndCheck(2, 317) {
        part2(puzzleInput)
    }
}

data class PipeContainer(val value: Pipe, val original: GridCellScreen = GridCellScreen(0, 0))

fun PipeContainer.isConnected(other: PipeContainer): Boolean {
    val x = other.original - this.original
    val allowed = this.value.connectionOffsets
    return x in allowed
}

class PipeGrid(var gridMap: Map<GridCellScreen, PipeContainer>, val offset: GridCellScreen = GridCellScreen.ORIGIN) {
    val rowMin = if (gridMap.isEmpty()) 0 else gridMap.keys.minOf { it.row }
    val rowMax = if (gridMap.isEmpty()) 0 else gridMap.keys.maxOf { it.row }
    val columnMin = if (gridMap.isEmpty()) 0 else gridMap.keys.minOf { it.column }
    val columnMax = if (gridMap.isEmpty()) 0 else gridMap.keys.maxOf { it.column }

    fun toPrintableStringWithDefault(): String =
        buildString {
            for (row in rowMin..rowMax) {
                for (column in columnMin..columnMax) {
                    val cell = gridMap[GridCellScreen(row, column)]
                    append(cell?.value ?: '.')
                }
                appendLine()
            }
        }.removeSuffix(System.lineSeparator())


    companion object {
        fun of(input: List<String>, offset: GridCellScreen = GridCellScreen.ORIGIN, ignoreChar: Char = '.'): PipeGrid =
            input.mapIndexed { rowIndex, row ->
                row.mapIndexedNotNull { columnIndex, c ->
                    if (c == ignoreChar) {
                        null
                    } else {
                        val cell = GridCellScreen(rowIndex + offset.row, columnIndex + offset.column)
                        cell to PipeContainer(c.toPipe(), cell.copy())
                    }
                }
            }.flatten().toMap().let { PipeGrid(it, offset) }

        fun of(input: String, offset: GridCellScreen = GridCellScreen.ORIGIN, ignoreChar: Char = '.'): PipeGrid =
            of(input.lines(), offset, ignoreChar)
    }
}

fun Point2D.toGridCellScreen() = GridCellScreen(y, x)

sealed class Pipe {
    abstract val connectionOffsets: List<GridCellScreen>

    override fun toString(): String =
        when (this) {
            Start -> "S"             //""S"
            Vertical -> "│"             //"|"
            Horizontal -> "─"             //"-"
            NorthEast -> "└"             //"L"
            NorthWest -> "┘"             //"J"
            SouthEast -> "┌"             //"F"
            SouthWest -> "┐"             //"7"
            None -> "@"             //"@"
        }

    object Start : Pipe() {
        override val connectionOffsets = listOf(
            CompassDirectionScreen.North.offset.toGridCellScreen(),
            CompassDirectionScreen.South.offset.toGridCellScreen(),
            CompassDirectionScreen.West.offset.toGridCellScreen(),
            CompassDirectionScreen.East.offset.toGridCellScreen()
        )
    }

    object Vertical : Pipe() {
        override val connectionOffsets = listOf(
            CompassDirectionScreen.North.offset.toGridCellScreen(),
            CompassDirectionScreen.South.offset.toGridCellScreen()
        )
    }

    object Horizontal : Pipe() {
        override val connectionOffsets = listOf(
            CompassDirectionScreen.West.offset.toGridCellScreen(),
            CompassDirectionScreen.East.offset.toGridCellScreen()
        )
    }

    object NorthEast : Pipe() {
        override val connectionOffsets = listOf(
            CompassDirectionScreen.North.offset.toGridCellScreen(),
            CompassDirectionScreen.East.offset.toGridCellScreen()
        )
    }

    object NorthWest : Pipe() {
        override val connectionOffsets = listOf(
            CompassDirectionScreen.North.offset.toGridCellScreen(),
            CompassDirectionScreen.West.offset.toGridCellScreen()
        )
    }

    object SouthEast : Pipe() {
        override val connectionOffsets = listOf(
            CompassDirectionScreen.South.offset.toGridCellScreen(),
            CompassDirectionScreen.East.offset.toGridCellScreen()
        )
    }

    object SouthWest : Pipe() {
        override val connectionOffsets = listOf(
            CompassDirectionScreen.South.offset.toGridCellScreen(),
            CompassDirectionScreen.West.offset.toGridCellScreen()
        )
    }

    object None : Pipe() {
        override val connectionOffsets = emptyList<GridCellScreen>()
    }
}

fun Char.toPipe(): Pipe =
    toString().toPipe()

fun String.toPipe(): Pipe =
    when (this) {
        "S" -> Pipe.Start
        "|" -> Pipe.Vertical
        "-" -> Pipe.Horizontal
        "L" -> Pipe.NorthEast
        "J" -> Pipe.NorthWest
        "F" -> Pipe.SouthEast
        "7" -> Pipe.SouthWest
        else -> throw IllegalArgumentException("No such Pipe symbol $this")
    }
