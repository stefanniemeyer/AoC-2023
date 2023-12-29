package de.niemeyer.aoc.utils

import de.niemeyer.aoc.utils.Resources.toResourcePath
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.Path

private const val YEAR = 2023

fun assertDayFile(fileName: String) {
    val day = fileName.filter(Char::isDigit).toInt()
    val file = File(fileName.toResourcePath())

    if (!file.exists()) {
        downloadInputFile(day, file.toPath())
    }
}

private fun downloadInputFile(day: Int, path: Path) {
    val cookie = File("aoc_cookie").readText().trim() // format is "session=..."
    val request = HttpRequest.newBuilder(URI("https://adventofcode.com/$YEAR/day/$day/input"))
        .header("Cookie", cookie)
        .header("accept", "text/plain")
        .GET()
        .build()

    val response = HttpClient.newHttpClient()
        .send(request, BodyHandlers.ofFile(path))

    if (response.statusCode() != 200) {
        throw IllegalArgumentException("Failed to download input file: Http ${response.statusCode()}")
    }
}
