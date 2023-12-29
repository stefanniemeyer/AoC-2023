@file:Suppress("unused")

package de.niemeyer.aoc.points

fun parsePoint2dMapTopLeft(input: String): Map<Point2D, Boolean> =
    parsePoint2dMapTopLeft(input.lines())

fun parsePoint2dMapTopLeft(input: List<String>): Map<Point2D, Boolean> {
    val rowProgression = input.indices step 1
    return parsePoint2dMap(input, rowProgression)
}

fun parsePoint2dMapBottomLeft(input: String): Map<Point2D, Boolean> =
    parsePoint2dMapBottomLeft(input.lines())

fun parsePoint2dMapBottomLeft(input: List<String>): Map<Point2D, Boolean> {
    val rowProgression = (input.size - 1) downTo 0
    return parsePoint2dMap(input, rowProgression)
}

fun parsePoint2dMap(input: List<String>, rowProgression: IntProgression): Map<Point2D, Boolean> =
    buildMap {
        rowProgression.forEach { y ->
            input[y].forEachIndexed { x, c ->
                if (c == '#' || c == '.') {
                    put(Point2D(x, y), c == '#')
                } else {
                    error("unexpected char '$c'")
                }
            }
        }
    }

fun parsePoint2dSetTopLeft(input: String): Set<Point2D> =
    parsePoint2dSetTopLeft(input.lines())

fun parsePoint2dSetTopLeft(input: List<String>): Set<Point2D> {
    val rowProgression = input.indices step 1

    return parsePoint2dSet(input, rowProgression)
}

fun parsePoint2dSetBottomLeft(input: String): Set<Point2D> =
    parsePoint2dSetBottomLeft(input.lines())

fun parsePoint2dSetBottomLeft(input: List<String>): Set<Point2D> {
    val rowProgression = (input.size - 1) downTo 0
    return parsePoint2dSet(input, rowProgression)
}

fun parsePoint2dSet(input: List<String>, rowProgression: IntProgression, relevantChar: Char = '#'): Set<Point2D> =
    buildSet {
        rowProgression.forEachIndexed { y, lineIdx ->
            input[lineIdx].mapIndexedNotNull { x, c ->
                if (c == relevantChar) add(Point2D(x, y))
            }
        }
    }
