@file:Suppress("unused")

package de.niemeyer.aoc.utils

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.absoluteValue
import kotlin.time.measureTimedValue

/**
 * Converts string to md5 hash.
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

tailrec fun Int.gcd(other: Int): Int {
    return if (this == 0 || other == 0) {
        this + other
    } else {
        val bigger = maxOf(this.absoluteValue, other.absoluteValue)
        val smaller = minOf(this.absoluteValue, other.absoluteValue)
        (bigger % smaller).gcd(smaller)
    }
}

fun Int.lcm(other: Int) =
    if (this == 0 || other == 0) {
        0
    } else {
        (this * other).absoluteValue / this.gcd(other)
    }

fun formatDuration(duration: kotlin.time.Duration): String {
    val minutes = duration.inWholeMinutes
    val seconds = duration.inWholeSeconds % 60
    val milliseconds = duration.inWholeMilliseconds % 1_000
    return String.format("%,9d%s", duration.inWholeMilliseconds, "ms") + listOf(minutes, seconds, milliseconds).zip(listOf("min", "s", "ms"))
        .mapNotNull { (time, unit) ->
            (String.format("%,d%s", time, unit)).takeIf { time > 0L }
        }.joinToString(" ")
        .let {
            if (it.isBlank()) {
                ""
            } else {
                " = ${it}"
            }
        }
}

fun <T> executeAndCheck(part: Int, expected: T, block: () -> T) {
    val result = measureTimedValue { block() }
    check(result.value == expected)
    println(result.value)
    println("Part ${part} took: ${formatDuration(result.duration)}")
}
