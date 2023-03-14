package org.needle4k.configuration

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ClassListParserTest {
  private val configuration = DefaultNeedleConfiguration()

  @Test
  fun shouldLoadInjectionProviders() {
    val lookupCustomClasses = ClassListParser(configuration)
    val providers = HashSet<Any>(lookupCustomClasses.lookup<Any>(KEY))

    assertThat(providers).containsExactlyInAnyOrder(String::class.java, Integer::class.java)
  }

  companion object {
    private const val KEY = "FOO"
  }
}