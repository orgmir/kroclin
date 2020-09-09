# kroclin

A snapshot testing library for kotlin, based on [pointfreeco/swift-snapshot-testing][swift-snapshot].

```kotlin
potatoes.assertSnapshot()
```

Useful when testing view model code, saves you from having to copy your expected data into your tests.

## Download

```groovy
    testImplementation 'dev.luisramos.kroclin:snapshot:0.1.0'
```

<details>
<summary>Snapshots of the development version are available in Sonatype's snapshots repository.</summary>
<p>

```groovy
repositories {
  maven {
    url 'https://oss.sonatype.org/content/repositories/snapshots/'
  }
}
dependencies {
  testImplementation 'dev.luisramos.kroclin:snapshot:0.1.0-SNAPSHOT'
}
```

</p>
</details>

## Usage

Write your test and call the extension method on the object you want to snapshot:

```kotlin
@Test
fun testPotatoesAreSaved() {
    data class Potato(val size: Int)

    val potatoes = listOf(Potato(1), Potato(2), Potato(3))

    potatoes.assertSnapshot()
}
```

Output will be a file `module/__snapshots__/com/example/ClassNameTest/testPotatoesAreSaved.txt`:

```
[Potato(size=1), Potato(size=2), Potato(size=3)]
```

First time the test is run, it will automatically save a snapshot and fail the test. Next time the test runs, it will verify the snapshot against the stored file.

Under the hood, `assertSnapshot()` is an extension function declared like so:

```kotlin
fun <T> List<T>.assertSnapshot() = listSnapshot<T>().assertSnapshot(this)

fun <T> listSnapshot(): Snapshot<List<T>, String> = Snapshot(
	pathExtension = "txt",
	diffing = stringLinesDiffing(),
	snapshot = {
		it.toString()
	}
)

fun stringLinesDiffing() = Diffing(
    toData = { it.toByteArray() },
    fromData = { it.toString(Charsets.UTF_8) },
    diff = { old, new ->
        val difference = diff(old.split("\n"), new.split("\n"))
        val hunks = chunk(difference)
        hunks
            .flatMap { listOf(it.patchMark) + it.lines }
            .joinToString("\n")
    }
)
```

You can create your own `Snapshot`s and your own extensions. For now, the serialization technique
used is the old fashioned `toString()` method, but you can extend it to use other serialization
libraries by creating your own `Diffing` methods. As it is, it works well enough with kotlin data classes.

## Thank you's

The architecture and naming scheme comes from the much more powerful [pointfreeco/swift-snapshot-testing][swift-snapshot], which I only wished it existed for Kotlin!

# License

    Copyright 2020 Luis Ramos

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[swift-snapshot]: https://github.com/pointfreeco/swift-snapshot-testing/
