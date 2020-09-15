package dev.luisramos.kroclin.snapshot.android

import android.view.View
import dev.luisramos.kroclin.snapshot.Snapshot
import dev.luisramos.kroclin.snapshot.assertSnapshot
import dev.luisramos.kroclin.snapshot.extensions.stringLinesDiffing
import radiography.ViewStateRenderers.DefaultsIncludingPii
import radiography.scan

fun viewSnapshot(): Snapshot<View, String> = Snapshot(
	pathExtension = "snap",
	diffing = stringLinesDiffing(),
	snapshot = { it.scan(viewStateRenderers = DefaultsIncludingPii) }
)

fun View.assertSnapshot() = viewSnapshot().assertSnapshot(this)