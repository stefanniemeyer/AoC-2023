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

fun <T> cyclicSequenceOf(list: List<T>): Sequence<T> = sequence {
    while (true) { // endless loop
        list.forEach { yield(it) }
    }
}
