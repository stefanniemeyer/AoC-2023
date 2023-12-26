/**
 * Advent of Code 2023, Day 19: Aplenty
 * Problem Description: https://adventofcode.com/2023/day/19
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsText
import de.niemeyer.aoc.utils.*

fun main() {
    fun solve(input: String): Long {
        val parts = input.split("\n\n").map { it.trim() }
        val workflows =
            parts.first().lines().map { Workflow.of(it) }.associateBy { it.name }
        val ratings = parts.last().lines().map { Rating.of(it) }
        val accepted = mutableSetOf<Rating>()
        ratings.forEach { rating ->
            var workflow = workflows.getValue("in")
            while (true) {
                val target = workflow.probe(rating) ?: error("What happened?")
                when (target) {
                    "A" -> {
                        accepted.add(rating)
                        break
                    }

                    "R" -> break
                    else -> workflow = workflows.getValue(target)
                }
            }
        }
        val res = accepted.flatMap { it.ratings.values }
        return res.sum().toLong()
    }

    fun part1(input: String): Long = solve(input)

    var workflows: Map<String, Workflow> = emptyMap()
    val solutions: MutableList<Pair<List<String>, Map<MachinePart, IntRange>>> = mutableListOf()

    fun dfs(path: List<String>, ranges: Map<MachinePart, IntRange>, ruleNum: Int = 0) {
        val workflowName = path.last()
        if (workflowName == "A") {
            solutions += path to ranges
            return
        } else if (workflowName == "R") {
            return
        }

        val workflow = workflows.getValue(workflowName)
        val rule = workflow.rules.drop(ruleNum).firstOrNull() ?: return
        if (rule.machinePart == MachinePart.None) {
            dfs(path + ruleNum.toString() + rule.target, ranges, 0)      // start at new workflow from the beginning
        } else {
            val accepted = rule.acceptedRange() intersect ranges.getValue(rule.machinePart)
            val accRanges = mutableMapOf(rule.machinePart to accepted)
            accRanges.putAll(ranges.filterNot { it.key == rule.machinePart })
            dfs(path + ruleNum.toString() + rule.target, accRanges, 0)       // jump to new workflow

            val nonAccepted = rule.nonAcceptedRange() intersect ranges.getValue(rule.machinePart)
            val nonAccRanges = mutableMapOf(rule.machinePart to nonAccepted)
            nonAccRanges.putAll(ranges.filterNot { it.key == rule.machinePart })
            dfs(path, nonAccRanges, ruleNum + 1)
        }

        return
    }

    fun part2(input: String): Long {
        solutions.clear()
        val parts = input.split("\n\n").map { it.trim() }
        workflows = parts.first().lines().map { Workflow.of(it) }.associateBy { it.name }
        val ranges = MachinePart.entries.filterNot { it == MachinePart.None }.associateWith { (1..4000) }
        dfs(listOf("in"), ranges)
        val x = solutions.map { solution ->
            solution.second.values.map { it.size().toLong() }.product()
        }.toList()
        val s = x.sum()
        return s
    }

    val name = getClassName()
    val testInput = resourceAsText(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsText(fileName = "${name}.txt")

    check(part1(testInput) == 19_114L)
    executeAndCheck(1, 386_787L) {
        part1(puzzleInput)
    }

    check(part2(testInput) == 167_409_079_868_000L)
    executeAndCheck(2, 131_029_523_269_531L) {
        part2(puzzleInput)
    }
}

enum class MachinePart(val symbol: Char) {
    ExtremelyCool('x'),
    Musical('m'),
    Aerodynamic('a'),
    Shiny('s'),
    None(' ')
}

fun Char.toMachinePart(): MachinePart =
    MachinePart.entries.firstOrNull { it.symbol == this } ?: error("Char '$this' is not valid for MachinePart")

data class Workflow(val name: String, val rules: List<Rule>) {
    fun probe(rating: Rating): String? =
        rules.firstNotNullOfOrNull { it.probe(rating) }

    companion object {
        fun of(input: String): Workflow {
            val name = input.substringBefore('{')
            val rules: List<Rule> = input.substringAfter('{').substringBefore('}').split(",").map { Rule.of(it) }
            return Workflow(name, rules)
        }
    }
}

enum class CompareFunc(val op: Char, val f: (Int, Int) -> Boolean) {
    GT('>', { a, b -> a > b }),
    LT('<', { a, b -> a < b }),
    NONE(' ', { _, _ -> false });
}

fun Char.toCompareFunc(): CompareFunc =
    CompareFunc.entries.firstOrNull { it.op == this } ?: error("Char '$this' is not valid for CompareFunc")

data class Rule(val machinePart: MachinePart, val cmpFun: CompareFunc, val arg: Int = 0, val target: String) {
    fun probe(rating: Rating): String? {
        if (machinePart == MachinePart.None) return target

        val ratingForPart = rating.ratings.getValue(machinePart)
        if (cmpFun.f(ratingForPart, arg)) {
            return target
        }
        return null
    }

    fun acceptedRange(): IntRange =
        when (cmpFun) {
            CompareFunc.LT -> (1..<arg)
            CompareFunc.GT -> (arg + 1..4000)
            else -> throw IllegalStateException("call of acceptedRange with $cmpFun")
        }

    fun nonAcceptedRange(): IntRange =
        when (cmpFun) {
            CompareFunc.LT -> (arg..4000)
            CompareFunc.GT -> (1..arg)
            else -> throw IllegalStateException("call of nonAcceptedRange with $cmpFun")
        }

    companion object {
        fun of(input: String): Rule {
            val regex = """(.)([<>])(\d+):(\w+)""".toRegex()
            val match = regex.find(input)

            if (match != null) {
                val (part, cmpOp, cmpArg, target) = match.destructured
                return Rule(
                    machinePart = part[0].toMachinePart(),
                    cmpFun = cmpOp[0].toCompareFunc(),
                    arg = cmpArg.toInt(),
                    target
                )
            } else {
                return Rule(MachinePart.None, CompareFunc.NONE, target = input)
            }
        }
    }
}

data class Rating(val ratings: Map<MachinePart, Int>) {
    companion object {
        fun of(input: String): Rating {
            val rating = input.drop(1).dropLast(1).split(",")
                .map { it.split("=") }
                .associate { (part, rating) -> part[0].toMachinePart() to rating.toInt() }
            return Rating(rating)
        }
    }
}
