package org.needle4k.injection

import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.needle4k.injection.InjectionProviders.providerForNamedInstance
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject
import javax.inject.Named

@Suppress("CdiInjectionPointsInspection")
class InjectionProvidersNamedInstanceInjectionProviderTest {
  open class SomeType

  private val providedNamedInstance = SomeType()

  @Rule
  @JvmField
  val needle = NeedleRule(providerForNamedInstance(FOO, providedNamedInstance))

  @Inject
  private lateinit var mockInstance: SomeType

  @Inject
  @field:Named(FOO)
  @SuppressWarnings("CdiInjectionPointsInspection")
  private lateinit var namedInstance: SomeType

  @Test
  fun shouldInjectNamedInstance() {
    Assertions.assertThat(Mockito.mockingDetails(namedInstance).isMock).isFalse
    Assert.assertEquals(providedNamedInstance, namedInstance)
  }

  @Test
  fun shouldInjectDefaultInstance() {
    Assertions.assertThat(Mockito.mockingDetails(mockInstance).isMock).isTrue
    Assert.assertNotEquals(mockInstance, providedNamedInstance)
  }

  companion object {
    private const val FOO = "foo"
  }
}