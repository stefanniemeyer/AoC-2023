/**
 * Advent of Code 2023, Day 15: Lens Library
 * Problem Description: https://adventofcode.com/2023/day/15
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsText
import de.niemeyer.aoc.utils.executeAndCheck
import de.niemeyer.aoc.utils.getClassName

fun calculateHash(input: String): Int =
    input.fold(0) { acc, c ->
        (acc + c.code) * 17 % 256
    }

fun main() {
    fun part1(input: String): Int =
        input
            .trim()
            .split(',')
            .sumOf { calculateHash(it) }

    fun part2(input: String): Int {
        val instr = input.trim().split(',')
        val labels = instr.map { Label.of(it) }
        val boxes = MutableList(256) { listOf<Label>() }
        for (label in labels) {
            if (label.operation == Operation.DASH) {
                boxes[label.hash] = boxes[label.hash].filter { it.text != label.text }
            } else {
                if (boxes[label.hash].any { it.text == label.text }) {
                    val newBox = boxes[label.hash].map {
                        if (it.text == label.text) label else it
                    }.toMutableList()
                    boxes[label.hash] = newBox
                } else {
                    boxes[label.hash] = (boxes[label.hash] + label).toMutableList()
                }
            }
        }
        val result = boxes.mapIndexed { idx, box ->
            val focPower = box.mapIndexed { labelIdx, label ->
                (idx + 1) * (labelIdx + 1) * label.focalLength
            }
            focPower.sum()
        }.sum()
        return result
    }

    val name = getClassName()
    val testInput = resourceAsText(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsText(fileName = "${name}.txt")

    check(part1(testInput) == 1_320)
    executeAndCheck(1, 510_273) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 145)
    executeAndCheck(2, 212_449) {
        part2(puzzleInput)
    }
}

enum class Operation(val value: Char) {
    DASH('-'),
    EQUAL_SIGN('=')
}

data class Label(val text: String, val hash: Int, val operation: Operation, val focalLength: Int) {
    override fun toString(): String =
        when (this.operation) {
            Operation.DASH -> "$text${operation.value}"
            Operation.EQUAL_SIGN -> "$text${operation.value}$focalLength"
        }

    companion object {
        fun of(input: String): Label {
            val re = """(\w+)([=-])(\w?)""".toRegex()
            val parts = re.matchEntire(input)
            val (text, op, num) = parts!!.destructured
            val hash = calculateHash(text)
            val operation =
                Operation.entries.find { it.value == op[0] } ?: error("Unexpected operation in '$input'")
            return when (operation) {
                Operation.DASH -> Label(text, hash, operation, 0)
                Operation.EQUAL_SIGN -> {
                    val focalLength = num.toInt()
                    Label(text, hash, operation, focalLength)
                }
            }
        }
    }
}
