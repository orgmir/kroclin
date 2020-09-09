package dev.luisramos.kroclin.extensions

import dev.luisramos.kroclin.*

fun linesSnapshot(): Snapshot<String, String> = Snapshot(
    pathExtension = "txt",
    diffing = stringLinesDiffing(),
    snapshot = { it }
)

fun stringLinesDiffing() = Diffing(
    toData = { it.toByteArray() },
    fromData = { it.toString(Charsets.UTF_8) },
    diff = { old, new ->
        val difference = diff(old.split("\n"), new.split("\n"))
        val hunks = chunk(difference)
        hunks
            .flatMap { listOf(it.patchMark) + it.lines }
            .joinToString("\n")
    }
)

fun String.assertSnapshotAsLines() = linesSnapshot().assertSnapshot(this)