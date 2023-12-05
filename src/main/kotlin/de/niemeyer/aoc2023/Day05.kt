/**
 * Advent of Code 2023, Day 05:
 * Problem Description: https://adventofcode.com/2023/day/05
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsText
import de.niemeyer.aoc.utils.getClassName

fun main() {
    fun seedsAndResources(input: String): Pair<List<Long>, List<Resource>> {
        val rules = input.split("\n\n").map { it.trim() }
        val seeds = rules.first().substringAfter(": ").split(' ').map { it.toLong() }
        val resources = rules.drop(1).map {
            Resource.ofString(it)
        }
        return seeds to resources
    }

    fun part1(input: String): Long {
        val (seeds, resources) = seedsAndResources(input)
        val solutions = seeds.map { seed ->
            resources.fold(seed) { acc, resource ->
                resource.convert(acc)
            }
        }
        return solutions.minOrNull() ?: error("What happened? No solution found.")
    }

    fun part2(input: String): Long {
        val (seeds, resources) = seedsAndResources(input)

        var minFound = Long.MAX_VALUE
        seeds.chunked(2)
            .forEach { seedChunk ->
                (seedChunk.first() until seedChunk.first() + seedChunk.last()).forEach { seed ->
                    val solution = resources.fold(seed) { acc, resource ->
                        resource.convert(acc)
                    }
                    if (solution < minFound) {
                        minFound = solution
                    }
                }
            }
        return minFound
    }

    val name = getClassName()
    val testInput = resourceAsText(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsText(fileName = "${name}.txt")

    check(part1(testInput) == 35L)
    val puzzleResultPart1 = part1(puzzleInput)
    println(puzzleResultPart1)
    check(puzzleResultPart1 == 173_706_076L)

    check(part2(testInput) == 46L)
    val puzzleResultPart2 = part2(puzzleInput)
    println(puzzleResultPart2)
    check(puzzleResultPart2 == 11_611_182L)
}

data class ResourceConversion(val sourceRangeStart: Long, val destinationRangeStart: Long, val rangeLength: Long)

class Resource(
    val conversions: List<ResourceConversion>
) {
    fun convert(startPoint: Long): Long {
        val x = conversions.mapNotNull { conversion ->
            if (startPoint in conversion.sourceRangeStart..<conversion.sourceRangeStart + conversion.rangeLength) {
                startPoint - conversion.sourceRangeStart + conversion.destinationRangeStart
            } else {
                null
            }
        }.firstOrNull()
        return x ?: startPoint
    }

    companion object {
        fun ofString(input: String): Resource {
            val lines = input.lines()
            val (sourceText, _, destinationText) = lines.first().split('-', ' ')
            val conversionList = lines.drop(1).map { line ->
                val (destinationStart, sourceStart, rangeLength) = line.split(' ').map { num ->
                    num.toLong()
                }
                ResourceConversion(sourceStart, destinationStart, rangeLength)
            }
            return Resource(
                conversionList
            )
        }
    }
}

