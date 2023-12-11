/**
 * Advent of Code 2023, Day 11:
 * Problem Description: https://adventofcode.com/2023/day/11
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName
import de.niemeyer.aoc.grid.Grid
import de.niemeyer.aoc.grid.GridCellScreen

fun main() {
    fun solve(input: List<String>, factor: Int = 2): Long =
        Grid.of(input, ignoreChar = '.').gridMap.keys.toSet().let { gridSet ->
            buildSet {
                var emptyRowCount = 0
                (gridSet.minOf { it.row }..gridSet.maxOf { it.row }).forEach { row ->
                    if (gridSet.none { it.row == row }) {
                        emptyRowCount  += factor - 1
                    } else {
                        gridSet.filter { it.row == row }.forEach {
                            add(GridCellScreen(it.row + emptyRowCount, it.column))
                        }
                    }
                }
            }
        }.let { rowsExpanded ->
            buildSet {
                var emptyColsCount = 0
                (rowsExpanded.minOf { it.column }..rowsExpanded.maxOf { it.column }).forEach { column ->
                    if (rowsExpanded.none { it.column == column }) {
                        emptyColsCount += factor - 1
                    } else {
                        rowsExpanded.filter { it.column == column }.forEach {
                            add(GridCellScreen(it.row, it.column + emptyColsCount))
                        }
                    }
                }
            }
        }.toList().let { expanded ->
            expanded.mapIndexed { idx, galaxy ->
                expanded.drop(idx + 1).map { next ->
                    galaxy.manhattanDistanceTo(next).toLong()
                }
            }
        }.flatten().sum()

    fun part1(input: List<String>): Long =
        solve(input, 2)

    fun part2(input: List<String>, factor: Int = 1_000_000): Long =
        solve(input, factor)

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 374L)
    executeAndCheck(1, 9_233_514L) {
        part1(puzzleInput)
    }

    check(part2(testInput, 10) == 1_030L)
    check(part2(testInput, 100) == 8_410L)
    executeAndCheck(2, 363_293_506_944L) {
        part2(puzzleInput)
    }
}
