/**
 * Advent of Code 2023, Day 04: Scratchcards
 * Problem Description: https://adventofcode.com/2023/day/04
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName

fun main() {
    fun part1(input: List<String>): Int =
        input.map {
            ScratchCardGame.of(it)
        }.mapNotNull { game ->
            (game.countNumbersWon - 1).takeIf { it >= 0 }
        }.sumOf { 1 shl it }

    fun part2(input: List<String>): Int {
        val games = input.map {
            ScratchCardGame.of(it)
        }
        val countGames = games.map { 1 to it.countNumbersWon }.toMutableList()
        countGames.forEachIndexed { idx, v ->
            ((idx + 1)..(idx + v.second)).forEach {
                countGames[it] = (v.first + countGames[it].first) to countGames[it].second
            }
        }
        return countGames.sumOf { it.first }
    }

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 13)
    executeAndCheck(1, 25_183) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 30)
    executeAndCheck(2, 5_667_240) {
        part2(puzzleInput)
    }
}

data class ScratchCardGame(val gameId: Int, val winning: Set<Int>, val scratchCard: Set<Int>) {
    private val numbersWon = scratchCard.intersect(winning)
    val countNumbersWon = numbersWon.size

    companion object {
        fun of(input: String): ScratchCardGame {
            val gameId = input.substringBefore(':').substringAfterLast(' ').toInt()
            val winning =
                input.substringAfter(':').substringBefore('|').trim().split("""\s+""".toRegex()).map { it.toInt() }
            val scratchCard = input.substringAfter('|').trim().split("""\s+""".toRegex()).map { it.toInt() }
            return ScratchCardGame(gameId, winning.toSet(), scratchCard.toSet())
        }
    }
}
