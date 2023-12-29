/**
 * Advent of Code 2023, Day 25: Snowverload
 * Problem Description: https://adventofcode.com/2023/day/25
 */

package de.niemeyer.aoc2023

import de.niemeyer.aoc.utils.Resources.resourceAsList
import de.niemeyer.aoc.utils.*
import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.DefaultUndirectedGraph

fun main() {
    fun part1(input: List<String>): Int {
        val graph = DefaultUndirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
        input.forEach { line ->
            val parts = line.split(':', ' ').filter { it.isNotBlank() }

            val root = parts.first()
            graph.addVertex(root)

            val nodes = parts.drop(1)
            nodes.forEach {
                graph.addVertex(it)
                graph.addEdge(root, it)
            }
        }

        val minCut = StoerWagnerMinimumCut(graph).minCut()
        graph.removeAllVertices(minCut)
        return graph.vertexSet().size * minCut.size
    }

    val name = getClassName()
    assertDayFile(fileName = "${name}.txt")
    val testInput = resourceAsList(fileName = "${name}_test.txt")
    val puzzleInput = resourceAsList(fileName = "${name}.txt")

    check(part1(testInput) == 54)
    executeAndCheck(1, 552_682) {
        part1(puzzleInput)
    }
}
