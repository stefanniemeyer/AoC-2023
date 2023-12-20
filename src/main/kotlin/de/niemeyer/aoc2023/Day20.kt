/**
 * Advent of Code 2023, Day 20: Pulse Propagation
 * Problem Description: https://adventofcode.com/2023/day/20
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*

fun main() {
//    fun testModules(): Long {
//        val btn = Button("button", listOf("broadcaster"))
//        println(btn.process(Pulse.LOW, "VOID"))
//        println(btn.process(Pulse.LOW, "VOID"))
//        println()
//
//        val rcv = Receiver("rx", listOf())
//        println(rcv.process(Pulse.LOW, "i1"))
//        println(rcv.process(Pulse.HIGH, "i2"))
//        println()
//
//        val bc = Broadcaster("broadcaster", listOf("a", "b"))
//        println(bc.process(Pulse.LOW, "button"))
//        println(bc.process(Pulse.LOW, "button"))
//        println(bc.process(Pulse.HIGH, "button"))
//        println(bc.process(Pulse.HIGH, "button"))
//        println(bc.process(Pulse.LOW, "button"))
//        println()
//
//        val ff = FlipFlop("ff", listOf("o1", "o2"))
//        println(ff.process(Pulse.HIGH, "broadcaster"))
//        println(ff.process(Pulse.LOW, "broadcaster"))
//        println(ff.process(Pulse.HIGH, "broadcaster"))
//        println(ff.process(Pulse.LOW, "broadcaster"))
//        println(ff.process(Pulse.LOW, "broadcaster"))
//        println(ff.process(Pulse.HIGH, "broadcaster"))
//        println(ff.process(Pulse.HIGH, "broadcaster"))
//        println()
//
//        val con = Conjunction("con", listOf("o1", "o2"))
//        con.incoming = listOf("i1", "i2")
//        println(con.process(Pulse.HIGH, "i1"))
//        println(con.process(Pulse.HIGH, "i2"))
//        println(con.process(Pulse.LOW, "i1"))
//        return 0L
//    }

    fun part1(input: List<String>): Long {
        val scheduler = Scheduler(input)
        val loops = 1000
        repeat(loops) {
            scheduler.pressButton()
        }
        val x= scheduler.getPulseCounts()
        val y = x.map { it.getValue(Pulse.LOW) to it.getValue(Pulse.HIGH) }
        val lowCount = y.sumOf { it.first }
        val highCount = y.sumOf { it.second }
        val res = lowCount * highCount
        return res
    }

    fun part2(input: List<String>): Long =
        TODO()

    val name = getClassName()
    val testInput = resourceAsList(fileName = "${name}_test.txt")
//    val testInput2 = resourceAsList(fileName = "${name}_test2.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

//    testModules(testInput)
    check(part1(testInput) == 32_000_000L)
//    check(part1(testInput2) == 0L)
    executeAndCheck(1, 867_118_762L) {
        part1(puzzleInput)
    }

//    check(part2(testInput) == 0)
//    executeAndCheck(2, 0) {
//        part2(puzzleInput)
//    }
}

class Scheduler(val input: List<String>) {
    private var modules = mutableMapOf<String, Module>()
    private var button: Button? = null

    init {
        val regex = """([&%]?)(\w+) -> (.*)""".toRegex()
        input.map { line ->
            val matchResult = regex.find(line) ?: error("Unexpected input line '$line'")

            val (type, moduleName, outgoingText) = matchResult.destructured
            val outgoing = outgoingText.split(", ")
            when (type) {
                "%" -> modules.put(moduleName, FlipFlop(moduleName, outgoing))
                "&" -> modules.put(moduleName, Conjunction(moduleName, outgoing))
                else -> {
                    if (moduleName == "broadcaster") {
                        modules.put(moduleName, Broadcaster(moduleName, outgoing))
                        modules.put("button", Button("button", listOf(moduleName)))
                    } else {
                        modules.put(moduleName, Receiver(moduleName, outgoing))
                    }
                }
            }
        }
        button = modules.values.firstOrNull { it is Button } as Button?
        val incommings = modules.entries.mapNotNull { mod ->
            mod.value.outgoing.map { con -> con to mod.key }.takeIf { it.isNotEmpty() }
        }.flatten().groupBy({ it.first }, { it.second })
        incommings.forEach {
            val module = modules.getOrPut(it.key) { Receiver(it.key, emptyList()) }
            if (module is Conjunction) {
                module.incoming = it.value
            }
        }
    }

    fun getPulseCounts() =
        modules.map { it.value.pulseCount}

    fun pressButton() {
        val signals = button?.process() ?: emptyList()
        val queue = ArrayDeque<Signal>(signals)
        while (queue.isNotEmpty()) {
            val signal = queue.removeFirst()
            val mod = modules.getValue(signal.target)
            queue += mod.process(signal.pulse, signal.source)
        }
    }
}

enum class Pulse(val text: String) {
    LOW("-low"),
    HIGH("-high")
}

data class Signal(val source: String, val pulse: Pulse, val target: String)

fun log(msg: String, level: Int = 1) {
    if (level <= 0)
        println(msg)
}

sealed class Module(val moduleName: String, val outgoing: List<String>) {
    val pulseCount = mutableMapOf(Pulse.LOW to 0L, Pulse.HIGH to 0L)

    open fun process(pulse: Pulse, source: String): List<Signal> = emptyList()
}

// Button module
//   push -> send low to broadcaster
class Button(moduleName: String, outgoing: List<String>) : Module(
    moduleName, outgoing
) {
    fun process(): List<Signal> =
        this.process(Pulse.LOW, "VOID")

    override fun process(pulse: Pulse, source: String): List<Signal> {
        log("CALL $moduleName", 2)
        pulseCount[pulse] = pulseCount.getValue(pulse) + outgoing.size
        return outgoing.map { out ->
            log("$moduleName ${pulse.text}-> $out ")
            Signal(moduleName, pulse, out)
        }
    }
}

// Receiver makes nothing
class Receiver(moduleName: String, outgoing: List<String>) : Module(
    moduleName, outgoing
) {
    override fun process(pulse: Pulse, source: String): List<Signal> {
        log("RECV $moduleName ${pulse.text} <- $source", 2)
        return emptyList()
    }
}

// Broadcaster module
//   sends received pulse to all destination modules
class Broadcaster(moduleName: String, outgoing: List<String>) : Module(
    moduleName, outgoing
) {
    override fun process(pulse: Pulse, source: String): List<Signal> {
        log("RECV $moduleName ${pulse.text} <- $source", 2)
        pulseCount[pulse] = pulseCount.getValue(pulse) + outgoing.size

        return outgoing.map { out ->
            log("$moduleName ${pulse.text}-> $out ")
            Signal(moduleName, pulse, out)
        }
    }
}

// FlipFlop (prefix %)
//   initially: memory=off
//   high -> NOTHING
//   low  -> memory = !memory
class FlipFlop(moduleName: String, outgoing: List<String>) : Module(
    moduleName, outgoing
) {
    private var state = false

    override fun process(pulse: Pulse, source: String): List<Signal> {
        log("RECV $moduleName ${pulse.text} <- $source", 2)

        return when (pulse) {
            Pulse.HIGH -> emptyList()
            Pulse.LOW -> {
                val next = if (state) Pulse.LOW else Pulse.HIGH
                pulseCount[next] = pulseCount.getValue(next) + outgoing.size
                state = !state
                outgoing.map { out ->
                    log("$moduleName ${next.text}-> $out ")
                    Signal(moduleName, next, out)
                }
            }
        }
    }
}

// Conjunction (prefix &)
//   remember most recent pulse from each input module
//   initially: memory=low for all modules
//   memory[input module] = pulse from input module
//   all pulses == high -> send low
//                     else send high
class Conjunction(moduleName: String, outgoing: List<String>) : Module(
    moduleName, outgoing
) {
    val pulseMemory = mutableMapOf<String, Pulse>()
    var incoming: List<String> = emptyList()
        set(inMods) {
            field = inMods
            inMods.forEach { pulseMemory[it] = Pulse.LOW }
        }

    override fun process(pulse: Pulse, source: String): List<Signal> {
        log("RECV $moduleName ${pulse.text} <- $source", 2)

        pulseMemory[source] = pulse
        val prevIncomingPulses = incoming.map { pulseMemory.getValue(it) }
        val allHigh = prevIncomingPulses.all { it == Pulse.HIGH }
        val next = if (allHigh) Pulse.LOW else Pulse.HIGH
        pulseCount[next] = pulseCount.getValue(next) + outgoing.size

        return outgoing.map { out ->
            log("$moduleName ${next.text}-> $out ")
            Signal(moduleName, next, out)
        }
    }
}
