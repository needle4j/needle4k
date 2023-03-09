package org.needle4k.testng

import org.junit.Assert
import org.testng.annotations.Test

class DatabaseTestNGTest : DatabaseTestcase() {
  @Test
  fun testGetDBAccess() {
    Assert.assertNotNull(entityManager)
    Assert.assertNotNull(entityManagerFactory)
  }
}