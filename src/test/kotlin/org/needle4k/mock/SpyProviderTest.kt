package org.needle4k.mock

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Spy
import org.needle4k.annotation.InjectInto
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject


class SpyProviderTest {
  @Rule
  @JvmField
  val needle= NeedleRule()

  @ObjectUnderTest
  @Spy
  private lateinit var a: A

  @ObjectUnderTest(implementation = BImpl::class)
  @InjectInto(targetComponentId = "a")
  @Spy
  private lateinit var b: B

  @Test
  fun shouldInjectSpyForA() {
    Mockito.`when`(b.name).thenReturn("world")
    Assert.assertEquals("hello world", a.hello())
    Mockito.verify(a).hello()
    Mockito.verify(b).name
  }

  @Test
  fun shouldNotRequestSpyWhenAnnotationIsNull() {
    val field = SpyProviderTest::class.java.getDeclaredField("b")
    Assert.assertFalse(SpyProvider.FAKE.isSpyRequested(field))
  }
}

interface B {
  val name: String
}

open class BImpl : B {
  override val name: String
    get() = "foo"
}

open class A {
  @Inject
  open lateinit var b: B

  open fun hello(): String {
    return "hello " + b.name
  }
}
