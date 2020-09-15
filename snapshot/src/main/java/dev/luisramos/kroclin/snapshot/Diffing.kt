package dev.luisramos.kroclin.snapshot

data class Diffing<T>(
    val toData: (T) -> ByteArray,
    val fromData: (ByteArray) -> T,
    val diff: (T, T) -> String
)