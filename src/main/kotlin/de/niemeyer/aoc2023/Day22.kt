/**
 * Advent of Code 2023, Day 22: Sand Slabs
 * Problem Description: https://adventofcode.com/2023/day/22
 * Disclaimer: Code by to Roman Elizarov w/ little refactoring
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.points.Point2D
import de.niemeyer.aoc.points.Point3D
import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    fun part1(input: List<String>): Int =
        Day22(input).solve1()

    fun part2(input: List<String>): Int =
        Day22(input).solve2()

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
    var bricks: List<Brick> = emptyList()

    constructor(input: List<String>) : this() {
        bricks = input.mapIndexed { idx, line ->
            Brick.of(line)
        }.sortedBy { it.a.z }
    }

    fun solve1(): Int {
        val numBricks = bricks.size
        val frameBuffer = Array(10) { IntArray(10) }
        val zBuffer = Array(10) { IntArray(10) { -1 } }
        val disintegratable = BooleanArray(numBricks) { true }
        for ((brickIdx, brick) in bricks.withIndex()) {
            val dx = (brick.b.x - brick.a.x).sign
            val dy = (brick.b.y - brick.a.y).sign
            val xyExpansion = IntRange(0, abs(brick.a.x - brick.b.x) + abs(brick.a.y - brick.b.y))
            val sz = xyExpansion.fold(0) { acc, i ->
                val p = Point2D(brick.a.x + i * dx, brick.a.y + i * dy)
                maxOf(acc, frameBuffer[p])
            }

            val support = HashSet<Int>()
            xyExpansion.forEach { i ->
                val p = Point2D(brick.a.x + i * dx,brick.a.y + i * dy)
                if (sz == frameBuffer[p] && zBuffer[p] >= 0) {
                    support += zBuffer[p]
                }
            }
            if (support.size == 1) disintegratable[support.single()] = false
            val nz = sz + 1 + brick.b.z - brick.a.z
            xyExpansion.forEach { i ->
                val p = Point2D(brick.a.x + i * dx, brick.a.y + i * dy)
                frameBuffer[p] = nz
                zBuffer[p] = brickIdx
            }
        }
        return disintegratable.count { it }
    }

    fun solve2(): Int {
        val numBricks = bricks.size
        val frameBuffer = Array(10) { IntArray(10) }
        val zBuffer = Array(10) { IntArray(10) { -1 } }
        val support = Array(numBricks) {HashSet<Int>() }
        for ((brickIdx, brick) in bricks.withIndex()) {
            val dx = (brick.b.x - brick.a.x).sign
            val dy = (brick.b.y - brick.a.y).sign
            val xyExpansion = IntRange(0, abs(brick.a.x - brick.b.x) + abs(brick.a.y - brick.b.y))
            val sz = xyExpansion.fold(0) { acc, i ->
                val p = Point2D(brick.a.x + i * dx, brick.a.y + i * dy)
                maxOf(acc, frameBuffer[p])
            }

            xyExpansion.forEach { i ->
                val p = Point2D(brick.a.x + i * dx,brick.a.y + i * dy)
                if (sz == frameBuffer[p]) {
                    support[brickIdx] += zBuffer[p]
                }
            }
            val nz = sz + 1 + brick.b.z - brick.a.z
            xyExpansion.forEach { i ->
                val p = Point2D(brick.a.x + i * dx, brick.a.y + i * dy)
                frameBuffer[p] = nz
                zBuffer[p] = brickIdx
            }
        }

        var result = 0
        for (ti in 0..<numBricks) {
            val ds = HashSet<Int>()
            ds += ti
            do {
                var changes = false
                for (tj in 0..<numBricks) if (tj !in ds && (support[tj] - ds).isEmpty()) {
                    ds += tj
                    changes = true
                }
            } while (changes)
            result += ds.size - 1
        }
        return result
    }
}

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
