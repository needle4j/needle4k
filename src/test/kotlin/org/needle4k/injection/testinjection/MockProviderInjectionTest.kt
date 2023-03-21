package org.needle4k.injection.testinjection

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.junit4.NeedleRule
import org.needle4k.mock.MockProvider
import javax.inject.Inject

@Suppress("CdiInjectionPointsInspection")
class MockProviderInjectionTest {
  @Rule
  @JvmField
  var needleRule = NeedleRule()

  @Inject
  private lateinit var mockProvider: MockProvider

  @Test
  fun testMockProviderInjection() {
    Assert.assertNotNull(mockProvider)
  }
}