package de.niemeyer.aoc.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import java.math.BigInteger

class UtilsTest {

    @Test
    @DisplayName("md5")
    fun testMd5() {
        val testcases = mapOf(
            "ab56b4d92b40713acc5af89985d4b786" to "abcde",
            "ebe596017db2f8c69136e5d6e594d365" to "01234567890",
            "76658de2ac7d406f93dfbe8bb6d9f549" to "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
            "5caa9f8c9c656b88039d573bd417cef1" to "()[]{}<>",
        )
        testcases.forEach { (k, v) ->
            assertThat(v.md5()).isEqualTo(k)
        }
    }

    @Nested
    inner class Day99Test {
        @Test
        @DisplayName("getClassName")
        fun testGetClassName() {
            assertThat(getClassName()).isEqualTo("UtilsTest\$Day99Test")
        }
    }

    @Test
    @DisplayName("gcd-Int")
    fun testGcdInt() {
        val testcases = mapOf(
            1 to Pair(2, 3),
            1 to Pair(4, 3),
            2 to Pair(6, 4),
            1 to Pair(13, 791),
            4 to Pair(10_284, 80),
            32 to Pair(128, 96),
        )
        testcases.forEach { (k, v) ->
            assertThat(v.first.gcd(v.second)).isEqualTo(k)
        }
    }

    @Test
    @DisplayName("lcm-Int")
    fun testLcmInt() {
        val testcases = mapOf(
            6 to Pair(2, 3),
            12 to Pair(4, 3),
            12 to Pair(6, 4),
            10_283 to Pair(13, 791),
        )
        testcases.forEach { (k, v) ->
            assertThat(v.first.lcm(v.second)).isEqualTo(k)
        }
    }

    @Test
    @DisplayName("gcd-Long")
    fun testGcdLong() {
        val testcases = mapOf(
            1L to Pair(2L, 3L),
            1L to Pair(4L, 3L),
            2L to Pair(6L, 4L),
            1L to Pair(13L, 791L),
            4L to Pair(10_284L, 80L),
            32L to Pair(128L, 96L),
        )
        testcases.forEach { (k, v) ->
            assertThat(v.first.gcd(v.second)).isEqualTo(k)
        }
    }

    @Test
    @DisplayName("lcm-Long")
    fun testLcmLong() {
        val testcases = mapOf(
            6L to Pair(2L, 3L),
            12L to Pair(4L, 3L),
            12L to Pair(6L, 4L),
            10_283L to Pair(13L, 791L),
        )
        testcases.forEach { (k, v) ->
            assertThat(v.first.lcm(v.second)).isEqualTo(k)
        }
    }

    @Test
    @DisplayName("gcd-BigInteger")
    fun testGcdBigInteger() {
        val testcases = mapOf(
            BigInteger("1") to Pair(BigInteger("2"), BigInteger("3")),
            BigInteger("1") to Pair(BigInteger("4"), BigInteger("3")),
            BigInteger("2") to Pair(BigInteger("6"), BigInteger("4")),
            BigInteger("1") to Pair(BigInteger("13"), BigInteger("791")),
            BigInteger("4") to Pair(BigInteger("10284"), BigInteger("80")),
            BigInteger("32") to Pair(BigInteger("128"), BigInteger("96")),
        )
        testcases.forEach { (k, v) ->
            assertThat(v.first.gcd(v.second)).isEqualTo(k)
        }
    }

    @Test
    @DisplayName("lcm-BigInteger")
    fun testLcmBigInteger() {
        val testcases = mapOf(
            BigInteger("6") to Pair(BigInteger("2"), BigInteger("3")),
            BigInteger("12") to Pair(BigInteger("4"), BigInteger("3")),
            BigInteger("12") to Pair(BigInteger("6"), BigInteger("4")),
            BigInteger("10283") to Pair(BigInteger("13"), BigInteger("791")),
        )
        testcases.forEach { (k, v) ->
            assertThat(v.first.lcm(v.second)).isEqualTo(k)
        }
    }
}
