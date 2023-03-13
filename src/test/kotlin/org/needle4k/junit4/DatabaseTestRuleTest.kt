package org.needle4k.junit4

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.junit4.testrule.DatabaseTestRule

class DatabaseTestRuleTest {
  @Rule
  @JvmField
  val databaseTestRule = DatabaseTestRule()

  @Test
  fun testEntityManager() {
    Assert.assertNotNull(databaseTestRule.entityManager)
  }
}