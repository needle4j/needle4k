package org.needle4k.injection.inheritance

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.MyComponent
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject

class InheritanceConstructorInjectionTest {
  @Rule
  @JvmField
  var rule = NeedleRule()

  @ObjectUnderTest
  private lateinit var derivedComponent: ConstructorInjectionDerivedComponent

  @Inject
  private lateinit var component: MyComponent

  @Test
  fun testFieldInjection_SameMockObject() {
    Assert.assertNotNull(derivedComponent)
    Assert.assertNotNull(component)
    Assert.assertSame(derivedComponent.myComponentFromBase, derivedComponent.component)
    Assert.assertSame(component, derivedComponent.component)
  }
}