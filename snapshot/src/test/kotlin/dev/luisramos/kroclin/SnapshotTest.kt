package dev.luisramos.kroclin

import dev.luisramos.kroclin.extensions.assertSnapshotAsLines
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class SnapshotTest {
	@Test
	fun `assertSnapshot should save file with correct contents`() {
		val file = Paths.get(
			"__snapshots__",
			"dev",
			"luisramos",
			"kroclin",
			"SnapshotTest",
			"assertSnapshot should save file with correct contents.txt"
		)
		Files.delete(file)

		val text = """
            Lorem Ipsum is simply dummy text of the printing and typesetting industry.
            Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, 
            when an unknown printer took a galley of type and scrambled it to make a type 
            specimen book. It has survived not only five centuries, but also the leap into 
            electronic typesetting, remaining essentially unchanged. It was popularised in 
            the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, 
            and more recently with desktop publishing software like Aldus PageMaker including 
            versions of Lorem Ipsum.
        """.trimIndent()

		text.assertSnapshotAsLines()

		assertThat(Files.exists(file), `is`(true))
	}
}