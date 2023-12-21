@file:Suppress("unused")

package de.niemeyer.aoc.utils

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.time.measureTimedValue

/**
 * Converts string to md5 hash
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

fun getClassName(): String =
    Throwable().stackTrace.first { it.className.contains("Day") }
        .className
        .split(".")
        .last()
        .removeSuffix("Kt")


fun formatDuration(duration: kotlin.time.Duration): String {
    val minutes = duration.inWholeMinutes
    val seconds = duration.inWholeSeconds % 60
    val milliseconds = duration.inWholeMilliseconds % 1_000
    return String.format("%,9d%s", duration.inWholeMilliseconds, "ms") + listOf(minutes, seconds, milliseconds).zip(
        listOf("min", "s", "ms")
    )
        .mapNotNull { (time, unit) ->
            (String.format("%,d%s", time, unit)).takeIf { time > 0L }
        }.joinToString(" ")
        .let {
            when {
                it.isBlank() -> ""
                else -> " = $it"
            }
        }
}

fun <T> executeAndCheck(part: Int, expected: T, block: () -> T) {
    val result = measureTimedValue { block() }
    println(result.value)
    println("Part ${part} took: ${formatDuration(result.duration)}")
    check(result.value == expected)
}

fun <T> Iterable<T>.repeatInfinite(): Sequence<T> = sequence {
    while (true) {
        yieldAll(this@repeatInfinite)
    }
}

/**
 * Takes a list of base/modulo combinations and returns the lowest number for which the states coincide such that:
 *
 * for all i: state(i) == base_state(i).
 *
 * E.g. chineseRemainder(listOf(3L to 4L, 5L to 6L, 2L to 5L)) == 47
 */
fun chineseRemainder(values: List<Pair<Long, Long>>): Long {
    if (values.isEmpty()) {
        return 0L
    }
    var result = values[0].first
    var leastCommon = values[0].second
    for (i in 1 until values.size) {
        val (base, modulo) = values[i]
        val target = base % modulo
        while (result % modulo != target) {
            result += leastCommon
        }
        leastCommon = leastCommon.lcm(modulo)
    }
    return result
}

fun chineseRemainder(rests: List<Long>, moduli: List<Long>): BigInteger {
    val product = moduli.map { it.toBigInteger() }.reduce(BigInteger::times)
    var sum = BigInteger.ZERO

    for (i in rests.indices) {
        val p = product / moduli[i].toBigInteger()
        sum += rests[i].toBigInteger() * p * p.modInverse(moduli[i].toBigInteger())
    }

    return sum % product
}
