package org.needle4k.postconstruct

import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.needle4k.NeedleInjector
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.annotation.Mock
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.junit4.NeedleRule
import org.needle4k.mock.EasyMockProvider
import org.needle4k.mock.MockProvider
import javax.inject.Inject

@Suppress("unused")
class PostConstructMockTest {
  private val needleInjector = object : NeedleInjector(InjectionConfiguration(DefaultNeedleConfiguration.INSTANCE)) {
    override fun beforePostConstruct() {
      dependentComponent.count()
    }
  }

  @Rule
  @JvmField
  val needleRule = NeedleRule(needleInjector)

  @InjectIntoMany
  @Mock
  private lateinit var dependentComponent: DependentComponent

  @ObjectUnderTest(postConstruct = true)
  private lateinit var componentWithPostConstruct: ComponentWithPrivatePostConstruct

  @Inject
  private lateinit var mockProvider: MockProvider

  private val easymockProvider: EasyMockProvider get() = mockProvider as EasyMockProvider

  @Test
  fun testPostConstruct_InjectIntoMany() {
    verify(dependentComponent, times(2)).count()
  }
}