package org.needle4k.junit5

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.needle4k.junit4.AbstractNeedleTest

@ExtendWith(value = [NeedleExtension::class])
class NeedleJUnit5Test : AbstractNeedleTest() {
  @Test
  override fun testBasicInjection() {
    super.testBasicInjection()
  }
}