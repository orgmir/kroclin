package dev.luisramos.kroclin

import dev.luisramos.kroclin.Difference.Which

data class Difference<T>(
    val elements: List<T>,
    val which: Which
) {
    enum class Which {
        FIRST, SECOND, BOTH
    }
}

private data class OverlapAcc(
    val overlap: MutableMap<Int, Int> = mutableMapOf(),
    val first: Int = 0,
    val second: Int = 0,
    val length: Int = 0
)

@OptIn(ExperimentalStdlibApi::class)
fun <T : Any> diff(first: List<T>, second: List<T>): List<Difference<T>> {
    val indexesOf = mutableMapOf<T, Array<Int>>()
    first.forEachIndexed { index, item ->
        indexesOf[item] = (indexesOf[item] ?: emptyArray()) + index
    }

    val sub = second.scanIndexed(OverlapAcc()) { index, sub, item ->
        indexesOf[item].orEmpty().scan(
            OverlapAcc(first = sub.first, second = sub.second, length = sub.length)
        ) { innerSub, firstIndex ->
            val newOverlap = innerSub.overlap.toMutableMap()
            newOverlap[firstIndex] = (sub.overlap[firstIndex - 1] ?: 0) + 1
            val newLength = newOverlap[firstIndex]
            if (newLength != null && newLength > sub.length) {
                OverlapAcc(
                    newOverlap,
                    firstIndex - newLength + 1,
                    index - newLength + 1,
                    newLength
                )
            } else {
                innerSub.copy(overlap = newOverlap)
            }
        }.last()
    }.last()
    val (_, firstIndex, secondIndex, length) = sub

    return if (length == 0) {
        val firstDiff = if (first.isEmpty()) emptyList() else listOf(Difference(first, Which.FIRST))
        val secondDiff =
            if (second.isEmpty()) emptyList() else listOf(Difference(second, Which.SECOND))
        firstDiff + secondDiff
    } else {
        val firstDiff = diff(first.slice(0 until firstIndex), second.slice(0 until secondIndex))
        val middleDiff =
            listOf(
                Difference(
                    first.slice(firstIndex until first.size).slice(0 until length),
                    Which.BOTH
                )
            )
        val lastDiff = diff(
            first.slice(firstIndex + length until first.size),
            second.slice(secondIndex + length until second.size)
        )
        return firstDiff + middleDiff + lastDiff
    }
}

private const val FIGURE_SPACE = "\u2007"

data class Hunk(
    val firstIndex: Int = 0,
    val firstLength: Int = 0,
    val secondIndex: Int = 0,
    val secondLength: Int = 0,
    val lines: List<String> = emptyList()
)

fun hunkOf(index: Int = 0, length: Int = 0, lines: List<String> = emptyList()) = Hunk(
    firstIndex = index,
    firstLength = length,
    secondIndex = index,
    secondLength = length,
    lines = lines
)

val Hunk.patchMark: String
    get() {
        val firstMark = "-${firstIndex + 1}$firstLength"
        val secondMark = "+${secondIndex + 1}$secondLength"
        return "@@ $firstMark $secondMark @@"
    }

operator fun Hunk.plus(rhs: Hunk): Hunk = Hunk(
    firstIndex = firstIndex + rhs.firstIndex,
    firstLength = firstLength + rhs.firstLength,
    secondIndex = secondIndex + rhs.secondIndex,
    secondLength = secondLength + rhs.secondLength,
    lines = lines + rhs.lines
)

fun chunk(diffs: List<Difference<String>>, context: Int = 4): List<Hunk> {
    fun prepending(prefix: String): (String) -> String = {
        prefix + it + if (it.endsWith(" ")) "¬" else ""
    }

    val changed: (Hunk) -> Boolean = { hunk ->
        hunk.lines.any { it.startsWith("-") || it.startsWith("+") }
    }

    val (hunk, hunks) = diffs.scan(Hunk() to emptyList<Hunk>()) { cursor, diff ->
        val (current, hunks) = cursor
        val length = diff.elements.size

        when {
            diff.which == Which.BOTH && length > context * 2 -> {
                val hunk = current + hunkOf(
                    length = context,
                    lines = diff.elements.slice(0 until context).map(prepending(FIGURE_SPACE))
                )
                val next = Hunk(
                    firstIndex = current.firstIndex + current.firstLength + length - context,
                    firstLength = context,
                    secondIndex = current.secondIndex + current.secondLength + length - context,
                    secondLength = context,
                    lines = diff.elements.slice(context until diff.elements.size)
                        .map(
                            prepending(FIGURE_SPACE)
                        )
                )
                next to if (changed(hunk)) hunks + hunk else hunks
            }
            diff.which == Which.BOTH && current.lines.isEmpty() -> {
                val lines = diff.elements.slice(context until diff.elements.size)
                    .map(prepending(FIGURE_SPACE))
                val size = lines.size
                current + hunkOf(length - size, size, lines) to hunks
            }
            else -> when (diff.which) {
                Which.BOTH ->
                    current + hunkOf(
                        length = length,
                        lines = diff.elements.map(prepending(FIGURE_SPACE))
                    ) to hunks
                Which.FIRST ->
                    current + Hunk(
                        firstLength = length,
                        lines = diff.elements.map(prepending("-"))
                    ) to hunks
                Which.SECOND ->
                    current + Hunk(
                        secondLength = length,
                        lines = diff.elements.map(prepending("+"))
                    ) to hunks
            }
        }
    }.last()

    return if (changed(hunk)) hunks + hunk else hunks
}

