package org.needle4k.db.operation

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.db.JPAInjectorConfiguration

class ExecuteScriptOperationTest {
  val configuration = JPAInjectorConfiguration(DefaultNeedleConfiguration())
  private val executeScriptOperation = ExecuteScriptOperation(configuration)

  @Test
  fun testSetUpOperation() {
    executeScriptOperation.setUpOperation()

    configuration.execute {
      val tableNames = executeScriptOperation.getTableNames(it)
      assertThat(tableNames).containsExactlyInAnyOrder(
        "ADDRESS_TABLE",
        "NEEDLE_TEST_ADDRESS",
        "NEEDLE_TEST_PERSON",
        "NEEDLE_TEST_USER",
        "USER_TABLE"
      )
    }
    executeScriptOperation.tearDownOperation()

    configuration.execute {
      val tableNames = executeScriptOperation.getTableNames(it)
      assertThat(tableNames).containsExactlyInAnyOrder(
        "NEEDLE_TEST_ADDRESS",
        "NEEDLE_TEST_USER",
        "NEEDLE_TEST_PERSON"
      )
    }
  }
}