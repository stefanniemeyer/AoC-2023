/**
 * Advent of Code 2023, Day 24:
 * Problem Description: https://adventofcode.com/2023/day/24
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*
import kotlin.math.sign
import kotlin.math.round
import kotlin.properties.Delegates

fun main() {
    fun part1(input: List<String>, testArea: Boolean = true): Int =
        Day24(input).solve(testArea)

    fun part2(input: List<String>): Int =
        TODO()

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 2)
    executeAndCheck(1, 27_328) {
        part1(puzzleInput, testArea = false)
    }

//    check(part2(testInput) == 0)
//    executeAndCheck(2, 0) {
//        part2(puzzleInput)
//    }
}

class Day24() {
    private lateinit var stones: List<Hailstone>
    private val lowerBound = 200000000000000.0
    private val upperBound = 400000000000000.0
    private var minX by Delegates.notNull<Double>()
    private var minY by Delegates.notNull<Double>()
    private var maxX by Delegates.notNull<Double>()
    private var maxY by Delegates.notNull<Double>()


    constructor(input: List<String>) : this() {
        stones = input.map { Hailstone.of(it) }
        minX = stones.minOf { it.x }
        minY = stones.minOf { it.y }
        maxX = stones.maxOf { it.x }
        maxY = stones.maxOf { it.y }
    }

    private fun inArea(p: Point?, testArea: Boolean) =
        if (testArea) inXY(p) else inBounds(p)

    private fun inBounds(p: Point?): Boolean {
        if (p == null) return false
        val pxr = round(p.x)
        val pyr = round(p.y)
        return (pxr >= lowerBound) && pxr <= upperBound && pyr >= lowerBound && pyr <= upperBound
    }

    private fun inXY(p: Point?): Boolean {
        if (p == null) return false
        val pxr = round(p.x)
        val pyr = round(p.y)
        return (pxr >= minX) && pxr <= maxX && pyr >= minY && pyr <= maxY
    }

    fun solve(testArea: Boolean = true): Int {
        val x = stones.flatMapIndexed { idx, a ->
            stones.drop(idx + 1).map { b ->
                findIntersectionXY(a, b)
            }
        }.filter { it.third != null }
        val sol = x.filter { inArea(it.third, testArea) }
        return sol.size
    }
}

fun findIntersectionXY(stoneA: Hailstone, stoneB: Hailstone): Triple<Hailstone, Hailstone, Point?> {
    // Berechnung der Steigungen
    val ma = stoneA.vy / stoneA.vx
    val mb = stoneB.vy / stoneB.vx

    // Berechnung der y-Achsenabschnitte
    val ba = stoneA.y - ma * stoneA.x
    val bb = stoneB.y - mb * stoneB.x

    // Überprüfen, ob die Geraden parallel sind (gleiche Steigung)
    if (ma == mb) {
        return Triple(stoneA, stoneB, null) // Kein Schnittpunkt, da parallel oder identisch
    }

    // Berechnung des Schnittpunkts
    val x = (bb - ba) / (ma - mb)
    val y = ma * x + ba

    val sAx = sign(x - stoneA.x) == sign(stoneA.vx)
    val sAy = sign(y - stoneA.y) == sign(stoneA.vy)
    val sBx = sign(x - stoneB.x) == sign(stoneB.vx)
    val sBy = sign(y - stoneB.y) == sign(stoneB.vy)
    if (!(sAx && sAy && sBx && sBy)) {
        return Triple(stoneA, stoneB, null) // Treffen in der Vergangenheit
    }

    return Triple(stoneA, stoneB, Point(x, y))
}

data class Point(val x: Double, val y: Double)

data class Hailstone(val x: Double, val y: Double, val z: Double, val vx: Double, val vy: Double, val vz: Double) {
    companion object {
        fun of(input: String): Hailstone {
            val (coord, velo) = input.split(" @ ")
            val (x, y, z) = coord.split(',').map(String::trim).map(String::toDouble)
            val (vx, vy, vz) = velo.split(',').map(String::trim).map(String::toDouble)
            return Hailstone(x, y, z, vx, vy, vz)
        }
    }
}
