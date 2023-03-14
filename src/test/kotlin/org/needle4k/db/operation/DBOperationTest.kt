package org.needle4k.db.operation

import org.assertj.core.api.Assertions
import org.hibernate.JDBCException
import org.junit.Assert
import org.junit.Test
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.db.JPAInjectorConfiguration

class DBOperationTest {
  val configuration = JPAInjectorConfiguration(DefaultNeedleConfiguration())
  private val dbOperation: AbstractDBOperation = object : AbstractDBOperation(configuration) {
    override fun tearDownOperation() {}
    override fun setUpOperation() {}
  }

  @Test
  fun testGetTableNames() {
    configuration.execute {
      var tableNames: List<String> = dbOperation.getTableNames(it)
      Assertions.assertThat(tableNames).containsExactlyInAnyOrder(
        "NEEDLE_TEST_ADDRESS",
        "NEEDLE_TEST_PERSON",
        "NEEDLE_TEST_USER",
      )
      val statement = it.createStatement()
      dbOperation.executeScript("before.sql", statement)
      tableNames = dbOperation.getTableNames(it)
      Assert.assertTrue(tableNames.contains("USER_TABLE"))
      Assert.assertTrue(tableNames.contains("ADDRESS_TABLE"))
      dbOperation.executeScript("after.sql", statement)
    }
  }

  @Test(expected = JDBCException::class)
  fun testExecuteScript() {
    configuration.execute {
      it.createStatement().use { statement -> dbOperation.executeScript("exception.sql", statement) }
    }
  }
}