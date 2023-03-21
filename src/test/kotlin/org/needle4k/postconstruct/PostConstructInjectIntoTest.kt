package org.needle4k.postconstruct

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule

class PostConstructInjectIntoTest {
  @Rule
  @JvmField
  val needleRule = NeedleRule()

  @Suppress("unused")
  @ObjectUnderTest(postConstruct = true)
  private lateinit var componentWithPostConstruct: ComponentWithPrivatePostConstruct

  @InjectIntoMany
  @ObjectUnderTest(postConstruct = false)
  private lateinit var dependentComponent: DependentComponent

  @Test
  fun testPostConstruct_InjectIntoMany() {
    dependentComponent.count()

    // expect one call in postConstruct of ComponentWithPrivatePostConstruct, one call here
    Assert.assertEquals(dependentComponent.counter, 2)
  }
}