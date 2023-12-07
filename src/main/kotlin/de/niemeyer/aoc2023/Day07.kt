/**
 * Advent of Code 2023, Day 07: Camel Cards
 * Problem Description: https://adventofcode.com/2023/day/07
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName

fun main() {
    fun part1(input: List<String>): Long =
        input
            .map { Hand.ofString(it) }
            .sorted()
            .mapIndexed { idx, hand ->
                (idx + 1) * hand.bid
            }.sum()

    fun part2(input: List<String>): Long =
        input
            .map { it.replace('J', JOKER) }
            .map { Hand.ofString(it) }
            .sorted()
            .mapIndexed { idx, hand ->
                (idx + 1) * hand.bid
            }.sum()

    val tests = mapOf(
        "JJ234" to CARDTYPE.THREE,
        "JJJ34" to CARDTYPE.FOUR,
        "AA2KK" to CARDTYPE.TWO_PAIR,
        "A2345" to CARDTYPE.HIGH_CARD,
        "AA234" to CARDTYPE.ONE_PAIR,
        "AJ234" to CARDTYPE.ONE_PAIR,
        "AAA23" to CARDTYPE.THREE,
        "AAA23" to CARDTYPE.THREE,
        "AAJ23" to CARDTYPE.THREE,
        "AAAKK" to CARDTYPE.FULL_HOUSE,
        "AAJKK" to CARDTYPE.FULL_HOUSE,
        "AAAA2" to CARDTYPE.FOUR,
        "AAAJ2" to CARDTYPE.FOUR,
        "JJJJ2" to CARDTYPE.FIVE,
        "AAAAA" to CARDTYPE.FIVE,
        "JJJJJ" to CARDTYPE.FIVE,
        "AAJJA" to CARDTYPE.FIVE,
    )
    tests.forEach {
        check(Hand.ofString(it.key.replace('J', JOKER) + " 1").type == it.value)
    }

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

enum class CARDTYPE {
    FIVE,
    FOUR,
    FULL_HOUSE,
    THREE,
    TWO_PAIR,
    ONE_PAIR,
    HIGH_CARD
}

val JOKER = 'X'
val CardRanks = "AKQJT98765432${JOKER}".reversed().mapIndexed { idx, c -> c to idx }.toMap()

class Hand(val cards: String, val type: CARDTYPE, val bid: Long) : Comparable<Hand> {
    override fun compareTo(other: Hand): Int =
        other.type.compareTo(this.type).takeIf { it != 0 }
            ?: this.cards.zip(other.cards)
                .firstNotNullOfOrNull { (t, o) ->
                    CardRanks.getOrDefault(t, 0).compareTo(CardRanks.getOrDefault(o, 0))
                        .takeIf { it != 0 }
                } ?: 0

    companion object {
        fun ofString(input: String): Hand {
            val cards = input.substringBefore(" ")
            val type = typeOfCards(cards)
            val bid = input.substringAfter(" ").toLong()
            return Hand(cards, type, bid)
        }

        fun typeOfCards(cards: String): CARDTYPE {
            val stats = cards.filter { it != JOKER }.groupingBy { it }.eachCount()
            val jokers = cards.count { it == JOKER }
            val order = stats.values.sorted().reversed()
            val highCount = order.firstOrNull() ?: 0
            val secondCount = order.drop(1).firstOrNull() ?: 0
            return if (highCount + jokers == 5) {
                CARDTYPE.FIVE
            } else if (highCount + jokers == 4) {
                CARDTYPE.FOUR
            } else if (highCount + jokers == 3 && secondCount == 2) {
                CARDTYPE.FULL_HOUSE
            } else if (highCount + jokers == 3) {
                CARDTYPE.THREE
            } else if (highCount == 2 && secondCount == 2) {
                CARDTYPE.TWO_PAIR
            } else if (highCount + jokers == 2) {
                CARDTYPE.ONE_PAIR
            } else {
                CARDTYPE.HIGH_CARD
            }
        }
    }
}
