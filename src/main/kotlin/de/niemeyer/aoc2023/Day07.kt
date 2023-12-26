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
            .map { Hand.of(it) }
            .sortedWith(compareBy({ it.type }, { it.cards }))
//            .also { h -> println(h) }
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

enum class CardType { HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE, FULL_HOUSE, FOUR, FIVE }

const val JOKER = '0'

@Suppress("SpellCheckingInspection")
const val CardRanking = "${JOKER}23456789TJQKA"
val TransJoker = CardRanking.indexOf(JOKER).transform()

fun Int.transform() = (this + 'a'.code).toChar()

class Hand(val cards: String, val type: CardType, val bid: Long) {
    companion object {
        fun of(input: String): Hand {
            val (cardsRaw, bid) = input.split(" ")
            val cards = cardsRaw.map { card ->
                CardRanking.indexOf(card).transform()
            }.joinToString("")
            return Hand(cards, typeOfCards(cards), bid.toLong())
        }

        private fun typeOfCards(cards: String): CardType {
            val stats = cards.filter { it != TransJoker }.groupingBy { it }.eachCount()
            val jokers = cards.count { it == TransJoker }
            val counts = stats.values.sorted().reversed()
            val highCount = counts.getOrElse(0) { 0 }
            val secondCount = counts.getOrElse(1) { 0 }
            return when {
                highCount + jokers == 5 -> CardType.FIVE
                highCount + jokers == 4 -> CardType.FOUR
                highCount + jokers == 3 && secondCount == 2 -> CardType.FULL_HOUSE
                highCount + jokers == 3 -> CardType.THREE
                highCount == 2 && secondCount == 2 -> CardType.TWO_PAIR
                highCount + jokers == 2 -> CardType.ONE_PAIR
                else -> CardType.HIGH_CARD
            }
        }
    }
}
