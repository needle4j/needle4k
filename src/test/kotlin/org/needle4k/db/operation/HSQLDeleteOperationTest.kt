package org.needle4k.db.operation

import org.hibernate.exception.ConstraintViolationException
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.db.Address
import org.needle4k.db.DatabaseInjectorConfiguration
import org.needle4k.db.operation.hsql.HSQLDeleteOperation
import org.needle4k.junit4.DatabaseRule
import java.sql.Statement

class HSQLDeleteOperationTest {
  @JvmField
  @Rule
  var databaseRule: DatabaseRule = DatabaseRule()

  private val hsqlDeleteOperation = HSQLDeleteOperation(DatabaseInjectorConfiguration(DefaultNeedleConfiguration.INSTANCE))

  @Test
  fun testDisableReferentialIntegrity() {
    databaseRule.configuration.execute {
      it.createStatement().use { statement ->
        hsqlDeleteOperation.disableReferentialIntegrity(statement)
        insertAddressWithInvalidFk(statement)
      }
    }
  }

  @Test(expected = ConstraintViolationException::class)
  fun testEnableReferentialIntegrity() {
    databaseRule.configuration.execute {
      it.createStatement().use { statement ->
        hsqlDeleteOperation.enableReferentialIntegrity(statement)
        insertAddressWithInvalidFk(statement)
      }
    }
  }

  private fun insertAddressWithInvalidFk(statement: Statement) {
    val address = Address()
    databaseRule.configuration.transactionHelper.executeInTransaction {
      it.persist(address)
      it.flush()
    }
    val executeUpdate = statement.executeUpdate("UPDATE " + Address.TABLE_NAME + " SET person_id = 2")
    Assert.assertEquals(1, executeUpdate)
  }

  @Test
  @Throws(Exception::class)
  fun testDeleteContent() {
    databaseRule.configuration.execute {
      it.createStatement().use { statement ->
        databaseRule.configuration.transactionHelper.executeInTransaction { em -> em.persist(Address()) }

        val rs = statement.executeQuery("SELECT * FROM " + Address.TABLE_NAME)
        Assert.assertTrue(rs.next())
        val tableNames: MutableList<String> = ArrayList()
        tableNames.add(Address.TABLE_NAME)

        hsqlDeleteOperation.deleteContent(tableNames, statement)
        statement.executeQuery("select * from " + Address.TABLE_NAME)
        Assert.assertFalse(rs.next())
        rs.close()
        statement.close()
      }
    }
  }
}