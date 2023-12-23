/**
 * Advent of Code 2023, Day 22: Sand Slabs
 * Problem Description: https://adventofcode.com/2023/day/22
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.points.Point2D
import de.niemeyer.aoc.points.Point3D
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*

fun main() {
    fun part1(input: List<String>): Int =
        Day22(input).solve()

    fun part2(input: List<String>): Int =
        Day22(input).solve(chainReaction = true)

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 5)
    executeAndCheck(1, 432) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 7)
    executeAndCheck(2, 63_166) {
        part2(puzzleInput)
    }
}

class Day22() {
    private var bricks = emptyList<Brick>()

    constructor(input: List<String>) : this() {
        bricks = input.map { line ->
            Brick.of(line)
        }.sortedBy { it.a.z }
    }

    fun solve(chainReaction: Boolean = false): Int {
        val maxDim = bricks.maxOf { maxOf(it.a.x, it.a.y, it.b.x, it.b.y) } + 1
        val infinity = -1
        val zBuffer = Array(maxDim) { IntArray(maxDim) { infinity } }
        val frameBuffer = Array(maxDim) { IntArray(maxDim) }
        val disintegratable = BooleanArray(bricks.size) { true }
        val supportChain = Array(bricks.size) { HashSet<Int>() }
        bricks.forEachIndexed { brickIdx, brick ->
            val xyLayer = brick.xyLayer()

            val highestZInLayer = xyLayer.fold(0) { acc, p ->
                maxOf(acc, frameBuffer[p])
            }

            val support = HashSet<Int>()
            xyLayer.forEach {
                if (highestZInLayer == frameBuffer[it]) {
                    supportChain[brickIdx] += zBuffer[it]
                    if (zBuffer[it] > infinity) {
                        support += zBuffer[it]
                    }
                }
            }
            val nextZ = highestZInLayer + 1 + brick.b.z - brick.a.z
            xyLayer.forEach {
                frameBuffer[it] = nextZ
                zBuffer[it] = brickIdx
            }
            if (support.size == 1) {
                disintegratable[support.single()] = false
            }
        }
        if (!chainReaction)
            return disintegratable.count { it }

        var result = 0
        bricks.indices.forEach { brickIdx ->
            val ds = HashSet<Int>()
            ds += brickIdx
            do {
                var changes = false
                for (tj in bricks.indices) if (tj !in ds && (supportChain[tj] - ds).isEmpty()) {
                    ds += tj
                    changes = true
                }
            } while (changes)
            result += ds.size - 1
        }
        return result
    }
}

fun Brick.xyLayer(): List<Point2D> =
    Point2D(a.x, a.y).lineTo(Point2D(b.x, b.y))

data class Brick(val a: Point3D, val b: Point3D) {
    companion object {
        fun of(input: String): Brick {
            val (ax, bx) = input.split('~')
            var (a, b) = Point3D.of(ax) to Point3D.of(bx)
            if (a.z > b.z) {
                a = b.also { b = a }
            }
            return Brick(a, b)
        }
    }
}
