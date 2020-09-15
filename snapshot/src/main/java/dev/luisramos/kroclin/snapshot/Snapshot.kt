package dev.luisramos.kroclin.snapshot

import java.io.IOException
import java.nio.file.FileSystemAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

data class Snapshot<T, S>(
	val pathExtension: String,
	val diffing: Diffing<S>,
	val snapshot: (T) -> S
)

private val idMapCounter = mutableMapOf<String, Int>()

fun <T, S> Snapshot<T, S>.assertSnapshot(value: T) {
	//TODO support override record mode

	val (dirPath, fileName) = getDirPath()
	val filePath = Paths.get(dirPath.toFile().path, fileName)
	// TODO allow override directory

	// TODO Android will throw
	//  java.nio.file.FileSystemException: /__snapshots__: Read-only file system
	// 	figure out support for instrumentation tests
	try {
		Files.createDirectories(dirPath)
	} catch (e: FileSystemAlreadyExistsException) {
		// all g
	} catch (e: IOException) {
		throw e
	}

	val diffable = snapshot(value)

	if (!Files.exists(filePath)) {
		// first time taking a snapshot, save this
		val file = Files.createFile(filePath).toFile()
		file.writeBytes(diffing.toData(diffable))
		val errorMessage =
			"\n📷 Failing test since a new snapshot has been recorded.\n\nSnapshot path: ${file.absolutePath}\n\n"
		throw AssertionError(errorMessage)
	} else {
		val file = filePath.toFile()
		val data = file.readBytes()
		val reference = diffing.fromData(data)
		val failure = diffing.diff(reference, diffable)
		if (failure.isNotEmpty()) {
			val errorMessage =
				"\nSnapshot does not match reference.\n\nSnapshot path: ${file.absolutePath}\n\n$failure"
			throw AssertionError(errorMessage)
		}
	}
}

private fun <T, S> Snapshot<T, S>.getDirPath(): Pair<Path, String> {
	val packageName = Snapshot::class.java.`package`.toString().removePrefix("package ")
	// walk up the stacktrace until we find a package that is not our library
	// of course this fails for tests inside our library, so we have to
	// check for that
	val callerStacktrace = Thread.currentThread().stackTrace
		.drop(1)
		.first { !it.className.contains(packageName) }

	val methodName = callerStacktrace.methodName.orEmpty()
	val path = callerStacktrace.className.split(".").toTypedArray()

	// support multiple snapshots per test
	val key = path.joinToString() + methodName
	val counter = idMapCounter[key]
	idMapCounter[key] = (counter ?: 0) + 1
	val id = counter?.let { "_$it" } ?: ""

	return Paths.get("__snapshots__", *path) to "$methodName$id.$pathExtension"
}