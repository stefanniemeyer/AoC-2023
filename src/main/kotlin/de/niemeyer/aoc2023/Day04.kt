/**
 * Advent of Code 2023, Day 04: Scratchcards
 * Problem Description: https://adventofcode.com/2023/day/04
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.getClassName

fun main() {
    fun part1(input: List<String>): Int =
        input.map {
            ScratchCardGame.ofString(it)
        }.mapNotNull { game ->
            (game.countNumbersWon - 1).takeIf { it >= 0 }
        }.sumOf { 1 shl it }

    fun part2(input: List<String>): Int {
        val games = input.map {
            ScratchCardGame.ofString(it)
        }
        val countGames = games.map { 1 to it.countNumbersWon }.toMutableList()
        countGames.forEachIndexed { idx, v ->
            ((idx + 1) .. (idx + v.second)).forEach {
                countGames[it] = (v.first + countGames[it].first) to countGames[it].second
            }
        }
        return countGames.sumOf { it.first }
    }

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 13)
    val puzzleResultPart1 = part1(puzzleInput)
    println(puzzleResultPart1)
    check(puzzleResultPart1 == 25_183)

    check(part2(testInput) == 30)
    val puzzleResultPart2 = part2(puzzleInput)
    println(puzzleResultPart2)
    check(puzzleResultPart2 == 5_667_240)
}

data class ScratchCardGame(val gameId: Int, val winning: Set<Int>, val scratchCard: Set<Int>) {
    val numbersWon = scratchCard.intersect(winning)
    val countNumbersWon = numbersWon.size

    companion object {
        fun ofString(input: String): ScratchCardGame {
            val gameId = input.substringBefore(':').substringAfterLast(' ').toInt()
            val winning =
                input.substringAfter(':').substringBefore('|').trim().split("""\s+""".toRegex()).map { it.toInt() }
            val scratchCard = input.substringAfter('|').trim().split("""\s+""".toRegex()).map { it.toInt() }
            return ScratchCardGame(gameId, winning.toSet(), scratchCard.toSet())
        }
    }
}
