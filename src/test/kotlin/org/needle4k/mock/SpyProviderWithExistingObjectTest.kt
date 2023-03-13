package org.needle4k.mock

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Spy
import org.needle4k.annotation.InjectInto
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule

class SpyProviderWithExistingObjectTest {
  @Rule
  @JvmField
  val needle = NeedleRule()

  @ObjectUnderTest
  @Spy
  private lateinit var a: A

  // b becomes a spy, although it is already instantiated
  @ObjectUnderTest
  @InjectInto(targetComponentId = "a")
  @Spy
  private var b: B = BImpl()

  @Test
  fun shouldInjectSpyForA() {
    Mockito.`when`(b.name).thenReturn("world")
    assertEquals("hello world", a.hello())
    Mockito.verify(a).hello()
    Mockito.verify(b).name
  }
}
