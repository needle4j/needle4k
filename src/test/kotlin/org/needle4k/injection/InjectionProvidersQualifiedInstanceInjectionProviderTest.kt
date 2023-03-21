package org.needle4k.injection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.needle4k.MyConcreteComponent
import org.needle4k.injection.InjectionProviders.providerForQualifiedInstance
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject

@Suppress("CdiInjectionPointsInspection")
class InjectionProvidersQualifiedInstanceInjectionProviderTest {
  private val providedQualifiedInstance = MyConcreteComponent()

  @Rule
  @JvmField
  val needle: NeedleRule = NeedleRule(providerForQualifiedInstance(CurrentUser::class.java, providedQualifiedInstance))

  @Inject
  private lateinit var mockInstance: MyConcreteComponent

  @Inject
  @field:CurrentUser
  private lateinit var qualifiedInstance: MyConcreteComponent

  @Test
  fun shouldInjectNamedInstance() {
    assertEquals(qualifiedInstance, providedQualifiedInstance)
  }

  @Test
  fun shouldInjectDefaultInstance() {
    assertNotEquals(mockInstance, providedQualifiedInstance)
  }
}