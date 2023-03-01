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
    assertEquals("jdbc-custom", loadResourceAndDefault[JDBC_URL_KEY])
  }

  @Test
  fun `Use defaults`() {
    val objectUnderTest = ConfigurationLoader("not-existing")
    val loadResourceAndDefault = objectUnderTest.configProperties

    assertEquals("TestDataModel", loadResourceAndDefault[PERSISTENCE_UNIT_NAME_KEY])
    assertEquals("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", loadResourceAndDefault[JDBC_URL_KEY])
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