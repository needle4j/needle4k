package org.needle4k.db.operation

import org.hibernate.exception.ConstraintViolationException
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.needle4k.db.Address
import org.needle4k.db.JPAInjectorConfiguration
import org.needle4k.db.operation.hsql.HSQLDeleteOperation
import org.needle4k.junit4.NeedleRule
import java.sql.Statement
import javax.inject.Inject

@Suppress("CdiInjectionPointsInspection", "SqlNoDataSourceInspection", "SqlWithoutWhere")
class HSQLDeleteOperationTest {
  @JvmField
  @Rule
  val needleRule = NeedleRule().withJPAInjection()

  @Inject
  private lateinit var configuration: JPAInjectorConfiguration

  private lateinit var hsqlDeleteOperation : HSQLDeleteOperation

  @Before
  fun init() {
    hsqlDeleteOperation = HSQLDeleteOperation(configuration)
  }

  @Test
  fun testDisableReferentialIntegrity() {
    configuration.execute {
      it.createStatement().use { statement ->
        hsqlDeleteOperation.disableReferentialIntegrity(statement)
        insertAddressWithInvalidFk(statement)
      }
    }
  }

  @Test(expected = ConstraintViolationException::class)
  fun testEnableReferentialIntegrity() {
    configuration.execute {
      it.createStatement().use { statement ->
        hsqlDeleteOperation.enableReferentialIntegrity(statement)
        insertAddressWithInvalidFk(statement)
      }
    }
  }

  private fun insertAddressWithInvalidFk(statement: Statement) {
    val address = Address()
    needleRule.jpaInjectorConfiguration.transactionHelper.execute {
      it.persist(address)
      it.flush()
    }
    val executeUpdate = statement.executeUpdate("UPDATE " + Address.TABLE_NAME + " SET person_id = 2")
    Assert.assertEquals(1, executeUpdate)
  }

  @Test
  @Throws(Exception::class)
  fun testDeleteContent() {
    configuration.execute {
      it.createStatement().use { statement ->
        configuration.transactionHelper.execute { em -> em.persist(Address()) }

        val rs = statement.executeQuery("SELECT * FROM " + Address.TABLE_NAME)
        Assert.assertTrue(rs.next())
        val tableNames: MutableList<String> = ArrayList()
        tableNames.add(Address.TABLE_NAME)

        hsqlDeleteOperation.deleteContent(tableNames, statement)
        statement.executeQuery("SELECT * FROM " + Address.TABLE_NAME)
        Assert.assertFalse(rs.next())
        rs.close()
        statement.close()
      }
    }
  }
}