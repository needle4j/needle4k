package org.needle4k.injection

import org.junit.Assert
import org.junit.Test
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.mock.MockProvider
import org.needle4k.mock.MockitoProvider

class InjectionConfigurationTest {
  @Test
  fun testCreateMockProvider() {
    val injectionConfiguration = InjectionConfiguration(DefaultNeedleConfiguration())
    val mockProvider: MockProvider = injectionConfiguration.createMockProvider()

    Assert.assertTrue(mockProvider is MockitoProvider)
  }
}