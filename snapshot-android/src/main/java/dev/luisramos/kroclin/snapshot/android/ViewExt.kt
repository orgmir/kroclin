package dev.luisramos.kroclin.snapshot.android

import android.view.View
import dev.luisramos.kroclin.Snapshot
import dev.luisramos.kroclin.assertSnapshot
import dev.luisramos.kroclin.extensions.stringLinesDiffing
import radiography.ViewStateRenderers.DefaultsIncludingPii
import radiography.scan

fun viewSnapshot(): Snapshot<View, String> = Snapshot(
	pathExtension = "snap",
	diffing = stringLinesDiffing(),
	snapshot = { it.scan(viewStateRenderers = DefaultsIncludingPii) }
)

fun View.assertSnapshot() = viewSnapshot().assertSnapshot(this)