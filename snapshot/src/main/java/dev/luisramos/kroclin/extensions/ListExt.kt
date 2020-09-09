package dev.luisramos.kroclin.extensions

import dev.luisramos.kroclin.Snapshot
import dev.luisramos.kroclin.assertSnapshot

fun <T> listSnapshot(): Snapshot<List<T>, String> = Snapshot(
	pathExtension = "txt",
	diffing = stringLinesDiffing(),
	snapshot = {
		it.toString()
	}
)

fun <T> List<T>.assertSnapshot() = listSnapshot<T>().assertSnapshot(this)