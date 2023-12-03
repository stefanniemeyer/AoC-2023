/**
 * Advent of Code 2023, Day 03: Gear Ratios
 * Problem Description: https://adventofcode.com/2023/day/03
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.grid.GridCellScreen
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.getClassName

fun main() {
    fun part1(input: List<String>): Int {
        val rawSchematic = RawSchematic.ofString(input)
        val engine = Engine.ofRawSchematic(rawSchematic)
        val symbols = rawSchematic.rawSchematic.filter { (_, v) ->
            !v.isDigit()
        }
        val valid = engine.engine.filter { part ->
            part.fields.any {
                it.neighbors.any {
                    symbols.containsKey(it)
                }
            }
        }
        return valid.sumOf { it.partId }
    }

    fun part2(input: List<String>): Long {
        val rawSchematic = RawSchematic.ofString(input)
        val engine = Engine.ofRawSchematic(rawSchematic)
        val symbols = rawSchematic.rawSchematic.filter { (_, v) ->
            v == '*'
        }
        val valid = engine.engine.mapNotNull { part ->
            val validNumber = part.fields.mapNotNull { fieldOfNumber ->
                val x = fieldOfNumber.neighbors.firstOrNull {
                    symbols.containsKey(it)
                }
                (x to part.partId).takeIf { x != null }
            }.toSet().toList()
            if (!validNumber.isEmpty()) {
                validNumber
            } else {
                null
            }
        }.flatten()
        val sum = valid
            .groupBy { it.first }
            .filter { it.value.size == 2 }
            .mapValues { a ->
                a.value.map { it.second }
            }.values.toList()
            .sumOf { innerList ->
                innerList.fold(1L) { product, element -> product * element }
            }

        return sum
    }

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 4_361)
    val puzzleResultPart1 = part1(puzzleInput)
    println(puzzleResultPart1)
    check(puzzleResultPart1 == 527_369)

    check(part2(testInput) == 467_835L)
    val puzzleResultPart2 = part2(puzzleInput)
    println(puzzleResultPart2)
    check(puzzleResultPart2 == 73_074_886L)
}

data class RawSchematic(val rawSchematic: Map<GridCellScreen, Char>) {
    val rowMin = if (rawSchematic.isEmpty()) 0 else rawSchematic.keys.minOf { it.row }
    val rowMax = if (rawSchematic.isEmpty()) 0 else rawSchematic.keys.maxOf { it.row }
    val columnMin = if (rawSchematic.isEmpty()) 0 else rawSchematic.keys.minOf { it.column }
    val columnMax = if (rawSchematic.isEmpty()) 0 else rawSchematic.keys.maxOf { it.column }

    companion object {
        fun ofString(input: List<String>, ignoreChar: Char = '.'): RawSchematic {
            val schema = input.mapIndexed { row, line ->
                line.mapIndexedNotNull() { column, c ->
                    if (c == ignoreChar) {
                        null
                    } else {
                        GridCellScreen(row, column) to c
                    }
                }
            }.flatten().toMap()
            return RawSchematic(schema)
        }
    }
}

data class PartNumber(val partId: Int, val fields: Set<GridCellScreen>)

data class Engine(val engine: List<PartNumber>) {
    companion object {
        fun ofRawSchematic(schema: RawSchematic): Engine {
            val numbers = mutableListOf<PartNumber>()
            for (row in schema.rowMin..schema.rowMax) {
                var collecting = false
                var digits = ""
                var number = 0
                val fields = mutableSetOf<GridCellScreen>()
                for (column in schema.columnMin..schema.columnMax) {
                    val c = schema.rawSchematic.getOrDefault(GridCellScreen(row, column), '.')
                    if (collecting) {
                        if (c.isDigit()) {
                            digits = "${digits}${c}"
                            fields.add(GridCellScreen(row, column))
                        } else {
                            collecting = false
                            numbers.add(PartNumber(digits.toInt(), fields.toSet()))
                        }
                    } else if (c.isDigit()) {
                        collecting = true
                        digits = "${c}"
                        fields.clear()
                        fields.add(GridCellScreen(row, column))
                    }
                }
                if (collecting) {
                    numbers.add(PartNumber(digits.toInt(), fields.toSet()))
                }
            }
            return Engine(numbers)
        }
    }
}
