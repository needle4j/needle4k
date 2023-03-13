package org.needle4k.injection.inheritance

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.needle4k.MyComponent
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject

class InheritanceInjectionTest {
  @Rule
  @JvmField
  var rule = NeedleRule()

  @ObjectUnderTest
  private lateinit var derivedComponent: DerivedComponent

  @ObjectUnderTest
  @InjectIntoMany
  private lateinit var dependencyComponent: GraphDependencyComponent

  @Inject
  private lateinit var component: MyComponent

  @Test
  fun testFieldInjection_SameMockObject() {
    assertNotNull(derivedComponent)
    assertSame(
      derivedComponent.componentFromBaseByFieldInjection, derivedComponent.componentByFieldInjection
    )
    assertSame(component, derivedComponent.componentByFieldInjection)
  }

  @Test
  fun testSetterInjection_SameMockObject() {
    assertNotNull(derivedComponent)
    assertNotNull(derivedComponent.componentFromBaseBySetter)
    assertSame(derivedComponent.componentFromBaseBySetter, derivedComponent.componentBySetter)
    assertSame(component, derivedComponent.componentBySetter)
  }

  @Test
  fun testGraphInjection() {
    val componentByFieldInjection: MyComponent? = derivedComponent.componentByFieldInjection
    val component: MyComponent? = dependencyComponent.component

    assertSame(component, componentByFieldInjection)
  }
}