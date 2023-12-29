@file:Suppress("unused")

/**
 * This code is from https://github.com/Mistborn94/advent-of-code-2023/
 */
package de.niemeyer.aoc.search

import java.util.*

typealias NeighbourFunction<K> = (K) -> Iterable<K>
typealias CostFunction<K> = (K, K) -> Int
typealias HeuristicFunction<K> = (K) -> Int

/**
 * Implements A* search to find the shortest path between two vertices
 */
fun <K> findShortestPath(
    start: K,
    end: K,
    neighbours: NeighbourFunction<K>,
    cost: CostFunction<K> = { _, _ -> 1 },
    heuristic: HeuristicFunction<K> = { 0 }
): GraphSearchResult<K> = findShortestPathByPredicate(start, { it == end }, neighbours, cost, heuristic)

/**
 * Implements A* search to find the shortest path between two vertices using a predicate to determine the ending vertex
 */
fun <K> findShortestPathByPredicate(
    start: K,
    endFunction: (K) -> Boolean,
    neighbours: NeighbourFunction<K>,
    cost: CostFunction<K> = { _, _ -> 1 },
    heuristic: HeuristicFunction<K> = { 0 }
): GraphSearchResult<K> {
    val toVisit = PriorityQueue(listOf(ScoredVertex(start, 0, heuristic(start))))
    var endVertex: K? = null
    val seenPoints: MutableMap<K, SeenVertex<K>> = mutableMapOf(start to SeenVertex(0, null))

    while (endVertex == null) {
        if (toVisit.isEmpty()) {
            return GraphSearchResult(start, null, seenPoints)
        }

        val (currentVertex, currentScore) = toVisit.remove()
        endVertex = if (endFunction(currentVertex)) currentVertex else null

        val nextPoints = neighbours(currentVertex).filter { it !in seenPoints }
            .map { next -> ScoredVertex(next, currentScore + cost(currentVertex, next), heuristic(next)) }

        toVisit.addAll(nextPoints)
        seenPoints.putAll(nextPoints.associate { it.vertex to SeenVertex(it.score, currentVertex) })
    }

    return GraphSearchResult(start, endVertex, seenPoints)
}

fun <K> shortestPathToAll(
    start: K, neighbours: NeighbourFunction<K>, cost: CostFunction<K> = { _, _ -> 1 }
): GraphSearchResult<K> {
    val initialToVisit = listOf(ScoredVertex(start, 0, 0))
    val seenPoints = shortestPathToAll(initialToVisit, neighbours, cost)
    return GraphSearchResult(start, seenPoints)
}

fun <K> shortestPathToAllFromAny(
    start: Map<K, Int>,
    neighbours: NeighbourFunction<K>,
    cost: CostFunction<K> = { _, _ -> 1 },
): GraphSearchResult<K> {
    val initialToVisit = start.map { (k, v) -> ScoredVertex(k, v, 0) }
    val seenPoints = shortestPathToAll(initialToVisit, neighbours, cost)
    return GraphSearchResult(start.keys, seenPoints)
}

private fun <K> shortestPathToAll(
    initialToVisit: List<ScoredVertex<K>>, neighbours: NeighbourFunction<K>, cost: CostFunction<K>
): MutableMap<K, SeenVertex<K>> {
    val toVisit = PriorityQueue(initialToVisit)
    val seenPoints: MutableMap<K, SeenVertex<K>> =
        initialToVisit.associateTo(mutableMapOf()) { it.vertex to SeenVertex(it.score, null) }

    while (toVisit.isNotEmpty()) {
        val (currentVertex, currentScore) = toVisit.remove()

        val nextPoints = neighbours(currentVertex).filter { it !in seenPoints }
            .map { next -> ScoredVertex(next, currentScore + cost(currentVertex, next), 0) }

        toVisit.addAll(nextPoints)
        seenPoints.putAll(nextPoints.associate { it.vertex to SeenVertex(it.score, currentVertex) })
    }
    return seenPoints
}

class GraphSearchResult<K>(val start: Set<K>, private val end: K?, private val vertices: Map<K, SeenVertex<K>>) {

    constructor(start: K, end: K?, result: Map<K, SeenVertex<K>>) : this(setOf(start), end, result)
    constructor(start: K, result: Map<K, SeenVertex<K>>) : this(setOf(start), null, result)
    constructor(start: Set<K>, result: Map<K, SeenVertex<K>>) : this(start, null, result)

    private fun getScore(vertex: K): Int =
        vertices[vertex]?.cost ?: throw IllegalStateException("Result for $vertex not available")

    fun getScore(): Int = end?.let { getScore(it) } ?: throw IllegalStateException("No path found")

    fun getPath() = end?.let { getPath(it, emptyList()) } ?: throw IllegalStateException("No path found")
    fun getPath(end: K) = getPath(end, emptyList())
    fun getVertexInPath(end: K, startCondition: (K) -> Boolean) =
        getPathItem(end, startCondition) ?: throw IllegalStateException("No path found")

    fun seen(): Set<K> = vertices.keys
    fun end(): K = end ?: throw IllegalStateException("No path found")

    private tailrec fun getPath(endVertex: K, pathEnd: List<K>): List<K> {
        val previous = vertices[endVertex]?.prev

        return if (previous == null) {
            listOf(endVertex) + pathEnd
        } else {
            getPath(previous, listOf(endVertex) + pathEnd)
        }
    }

    private tailrec fun getStart(endVertex: K): K {
        val previous = vertices[endVertex]?.prev

        return if (previous == null) {
            endVertex
        } else {
            getStart(previous)
        }
    }

    private tailrec fun getPathItem(endVertex: K, startCondition: (K) -> Boolean = { it == null }): K? {
        val previous = vertices[endVertex]?.prev

        return if (previous == null) {
            null
        } else if (startCondition(previous)) {
            previous
        } else {
            getPathItem(previous, startCondition)
        }
    }
}

data class SeenVertex<K>(val cost: Int, val prev: K?)

data class ScoredVertex<K>(val vertex: K, val score: Int, val heuristic: Int) : Comparable<ScoredVertex<K>> {
    override fun compareTo(other: ScoredVertex<K>): Int = (score + heuristic).compareTo(other.score + other.heuristic)
}
