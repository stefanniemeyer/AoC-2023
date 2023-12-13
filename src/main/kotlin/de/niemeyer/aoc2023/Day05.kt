/**
 * Advent of Code 2023, Day 05:
 * Problem Description: https://adventofcode.com/2023/day/05
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsText
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() {
    fun seedsAndResources(input: String): Pair<List<Long>, List<Resource>> {
        val rules = input.split("\n\n").map { it.trim() }
        val seeds = rules.first().substringAfter(":").split(' ').mapNotNull { it.toLongOrNull() }
        val resources = rules.drop(1).map {
            Resource.of(it)
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

        val solution: Long
        runBlocking(Dispatchers.Default) {
            val all = seeds.chunked(2)
                .map { seedChunk ->
                    async {
                        var minFound = Long.MAX_VALUE
                        (seedChunk.first() until seedChunk.first() + seedChunk.last()).forEach { seed ->
                            val sol = resources.fold(seed) { acc, resource ->
                                resource.convert(acc)
                            }
                            if (sol < minFound) {
                                minFound = sol
                            }
                        }
                        minFound
                    }
                }.awaitAll()
            solution = all.min()
        }
        return solution
    }

    val name = getClassName()
    val testInput = resourceAsText(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsText(fileName = "${name}.txt")

    check(part1(testInput) == 35L)
    executeAndCheck(1, 173_706_076L) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 46L)
    executeAndCheck(2, 11_611_182L) {
        part2(puzzleInput)
    }
}

data class ResourceConversion(val sourceRangeStart: Long, val destinationRangeStart: Long, val rangeLength: Long)

class Resource(
    private val conversions: List<ResourceConversion>
) {
    fun convert(startPoint: Long): Long =
        conversions.firstNotNullOfOrNull { conversion ->
            if (startPoint in conversion.sourceRangeStart..<conversion.sourceRangeStart + conversion.rangeLength) {
                startPoint - conversion.sourceRangeStart + conversion.destinationRangeStart
            } else {
                null
            }
        } ?: startPoint

    companion object {
        fun of(input: String): Resource {
            val lines = input.lines()
            val conversionList = lines.drop(1).map { line ->
                val (destinationStart, sourceStart, rangeLength) = line.split(' ').map { num ->
                    num.toLong()
                }
                ResourceConversion(sourceStart, destinationStart, rangeLength)
            }
            return Resource(conversionList)
        }
    }
}

