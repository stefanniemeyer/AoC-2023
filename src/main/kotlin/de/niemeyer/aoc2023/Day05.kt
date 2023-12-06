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
        val seeds = rules.first().substringAfter(":").split(' ').mapNotNull { it.toLongOrNull() }
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
    val startTime1 = System.currentTimeMillis()
    val puzzleResultPart1 = part1(puzzleInput)
    val endTime1 = System.currentTimeMillis()
    val duration1 = endTime1 - startTime1
    println(puzzleResultPart1)
    println("Runtime for part 1: $duration1 ms")
    check(puzzleResultPart1 == 173_706_076L)

    check(part2(testInput) == 46L)
    val startTime2 = System.currentTimeMillis()
    val puzzleResultPart2 = part2(puzzleInput)
    val endTime2 = System.currentTimeMillis()
    val duration2 = endTime2 - startTime2
    println(puzzleResultPart2)
    println("Runtime for part 2: $duration2 ms")
    check(puzzleResultPart2 == 11_611_182L)
}

data class ResourceConversion(val sourceRangeStart: Long, val destinationRangeStart: Long, val rangeLength: Long)

class Resource(
    private val conversions: List<ResourceConversion>
) {
    fun convert(startPoint: Long): Long {
        val x = conversions.firstNotNullOfOrNull { conversion ->
            if (startPoint in conversion.sourceRangeStart..<conversion.sourceRangeStart + conversion.rangeLength) {
                startPoint - conversion.sourceRangeStart + conversion.destinationRangeStart
            } else {
                null
            }
        } ?: startPoint
        return x
    }

    companion object {
        fun ofString(input: String): Resource {
            val lines = input.lines()
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

