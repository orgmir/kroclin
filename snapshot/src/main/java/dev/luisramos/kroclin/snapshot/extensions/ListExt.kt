package dev.luisramos.kroclin.snapshot.extensions

import dev.luisramos.kroclin.snapshot.Snapshot
import dev.luisramos.kroclin.snapshot.assertSnapshot

fun <T> listSnapshot(): Snapshot<List<T>, String> = Snapshot(
	pathExtension = "txt",
	diffing = stringLinesDiffing(),
	snapshot = {
		it.toString()
	}
)

fun <T> List<T>.assertSnapshot() = listSnapshot<T>().assertSnapshot(this)