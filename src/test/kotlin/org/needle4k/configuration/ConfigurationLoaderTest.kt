package org.needle4k.configuration

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ConfigurationLoaderTest {
  @Test
  fun `Load custom properties`() {
    val objectUnderTest = ConfigurationLoader("needle-custom")
    val loadResourceAndDefault = objectUnderTest.configProperties

    assertNotNull(loadResourceAndDefault)
    assertEquals("TestDataModel", loadResourceAndDefault[PERSISTENCE_UNIT_NAME_KEY])
  }

  @Test
  fun `Use defaults`() {
    val objectUnderTest = ConfigurationLoader("not-existing")
    val loadResourceAndDefault = objectUnderTest.configProperties

    assertEquals("TestDataModel", loadResourceAndDefault[PERSISTENCE_UNIT_NAME_KEY])
  }

  @Test
  fun testLoadResource() {
    val loadResource = ConfigurationLoader.loadResource("needle.properties")
    assertNotNull(loadResource)
    val loadResourceWithLeadingSlash = ConfigurationLoader.loadResource("/needle.properties")
    assertNotNull(loadResourceWithLeadingSlash)
    assertNull(ConfigurationLoader.loadResource("notfound.properties"))
  }
}