package org.needle4k.injection

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule

class ShouldNotInjectStringTest {
  class InjectionTargetBean {
    var doNotInjectThis: String? = null
  }

  @Rule
  @JvmField
  var needleRule: NeedleRule = NeedleRule()

  @ObjectUnderTest
  private lateinit var injectionTargetBean: InjectionTargetBean

  @InjectIntoMany
  private val injectThis = "injectThis"

  private var doNotInjectThis: String? = null

  @Test
  fun should_not_inject_into_injectionTargetBean_doNotInjectThis() {
    Assert.assertNull(injectionTargetBean.doNotInjectThis)
  }

  @Test
  fun should_not_inject_into_testInstance_doNotInjectThis() {
    Assert.assertNull(doNotInjectThis)
  }
}