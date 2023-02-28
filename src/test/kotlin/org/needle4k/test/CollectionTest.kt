package org.needle4k.test

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class CollectionTest {
  @Test
  fun `flatMap work as expected`() {
    val numbers = listOf("one" to listOf(1, 2, 3), "two" to listOf(4, 3, 5), "three" to listOf(4))
    val allPairs = numbers.map { it.second.map { i -> it.first to i } }.flatten()
    val asMap = allPairs.groupBy({ pair -> pair.second }, { pair -> pair.first })

    Assertions.assertThat(asMap).hasSize(5)
      .containsEntry(1, listOf("one"))
      .containsEntry(2, listOf("one"))
      .containsEntry(3, listOf("one", "two"))
      .containsEntry(4, listOf("two", "three"))
      .containsEntry(5, listOf("two"))
  }
}