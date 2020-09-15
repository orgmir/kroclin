package dev.luisramos.kroclin.snapshot

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test


class DiffKtTest {
    @Test
    fun diff_test() {
        val text1 = """
            Lorem Ipsum is simply dummy text of the printing and typesetting industry.
            Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, 
            when an unknown printer took a galley of type and scrambled it to make a type 
            specimen book. It has survived not only five centuries, but also the leap into 
            electronic typesetting, remaining essentially unchanged. It was popularised in 
            the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, 
            and more recently with desktop publishing software like Aldus PageMaker including 
            versions of Lorem Ipsum.
        """.trimIndent()
        val text2 = """
            Lorem Ipsum is simply dummy text of the printing and typesetting industry.
            specimen book. It has survived not only five centuries, but also the leap into 
            electronic typesetting, remaining essentially unchanged. It was popularised in 
            the 1960s with the release of heloo there sheets containing Lorem Ipsum passages, 
            and more recently with desktop publishing software like Aldus PageMaker including 
            and more recently with desktop publishing software like Aldus PageMaker including 
            versions of Lorem Ipsum.
        """.trimIndent()

        val difference = diff(text1.split("\n"), text2.split("\n"))
        assertThat(difference.size, `is`(6))

        val hunks = chunk(difference)
        val failure = hunks
            .flatMap { listOf(it.patchMark) + it.lines }
            .joinToString("\n")
        val expected = """
            @@ -27 +26 @@
            -Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, ¬
            -when an unknown printer took a galley of type and scrambled it to make a type ¬
             specimen book. It has survived not only five centuries, but also the leap into ¬
             electronic typesetting, remaining essentially unchanged. It was popularised in ¬
            -the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, ¬
            +the 1960s with the release of heloo there sheets containing Lorem Ipsum passages, ¬
            +and more recently with desktop publishing software like Aldus PageMaker including ¬
             and more recently with desktop publishing software like Aldus PageMaker including ¬
             versions of Lorem Ipsum.
        """.trimIndent()
        assertThat(failure, `is`(expected))
    }
}