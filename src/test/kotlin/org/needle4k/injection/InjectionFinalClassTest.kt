package org.needle4k.injection

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule

class InjectionFinalClassTest {
  @Rule
  @JvmField
  val needleRule = NeedleRule()

  @ObjectUnderTest
  private lateinit var testClass: InjectionFinalClass

  @Test
  fun testFinal() {

    Assert.assertNull(testClass.string)
  }
}