package org.needle4k.injection

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule

class CustomInjectionAnnotationTest {
  @JvmField
  @Rule
  // CustomInjectionAnnotation1 is added by needle.properties
  var needleRule: NeedleRule = NeedleRule().apply {
    needleInjector.configuration.addGlobalInjectionAnnotationClass(CustomInjectionAnnotation2::class.java)
  }

  @ObjectUnderTest
  private lateinit var component: CustomInjectionTestComponent

  @Test
  fun testCustomInjection() {
    Assert.assertNotNull(component.queue1)
    Assert.assertNotNull(component.queue2)
    Assert.assertNotNull(component.map)
  }
}