/**
 * Advent of Code 2023, Day 07: Camel Cards
 * Problem Description: https://adventofcode.com/2023/day/07
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName

fun main() {
    fun solve(input: List<String>) =
        input
            .map { Hand.ofString(it) }
            .sortedWith(compareBy({ it.type }, { it.cards }))
            .mapIndexed { idx, hand ->
                (idx + 1) * hand.bid
            }.sum()

    fun part1(input: List<String>): Long =
        solve(input)

    fun part2(input: List<String>): Long =
        solve(input.map { it.replace('J', JOKER) })

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 6_440L)
    executeAndCheck(1, 252_052_080L) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 5_905L)
    executeAndCheck(2, 252_898_370L) {
        part2(puzzleInput)
    }
}

enum class CARDTYPE { FIVE, FOUR, FULL_HOUSE, THREE, TWO_PAIR, ONE_PAIR, HIGH_CARD }

const val JOKER = 'X'
const val CardRanking = "${JOKER}23456789TJQKA"

class Hand(val cards: String, val type: CARDTYPE, val bid: Long) {
    companion object {
        fun ofString(input: String): Hand {
            val (cardsRaw, bid) = input.split(" ")
            val cards = cardsRaw.map { card ->
                CardRanking
                    .indexOf(card)
                    .let { (it + 'A'.code).toChar() }
            }.joinToString("")
            return Hand(cards, typeOfCards(cards), bid.toLong())
        }

        private fun typeOfCards(cards: String): CARDTYPE {
            val stats = cards.filter { it != JOKER }.groupingBy { it }.eachCount()
            val jokers = cards.count { it == JOKER }
            val (highCount, secondCount) = stats.values.sorted().reversed()
            return when {
                highCount + jokers == 5 -> CARDTYPE.FIVE
                highCount + jokers == 4 -> CARDTYPE.FOUR
                highCount + jokers == 3 && secondCount == 2 -> CARDTYPE.FULL_HOUSE
                highCount + jokers == 3 -> CARDTYPE.THREE
                highCount == 2 && secondCount == 2 -> CARDTYPE.TWO_PAIR
                highCount + jokers == 2 -> CARDTYPE.ONE_PAIR
                else -> CARDTYPE.HIGH_CARD
            }
        }
    }
}
