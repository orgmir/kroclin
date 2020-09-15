package com.example.kroclin

import dev.luisramos.kroclin.extensions.assertSnapshot
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
			"com",
			"example",
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

		try {
			text.assertSnapshot()
		} catch (e: AssertionError) {
			// test fails on new created snapshot
		}

		assertThat(Files.exists(file), `is`(true))
	}

	@Test
	fun `list of data objects should be saved`() {
		data class Potato(val size: Int)

		val potatoes = listOf(Potato(1), Potato(2), Potato(3))

		potatoes.assertSnapshot()
	}

	@Test
	fun `multiple asserts per test should work`() {
		val sacredTexts = listOf("text1", "text2", "text3")
		sacredTexts.assertSnapshot()

		val notSoSacredTests = listOf("text5", "text4", "text3")
		notSoSacredTests.assertSnapshot()
	}
}