package org.needle4k.junit4

import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.needle4k.db.AbstractJPAEntityManagerTest
import org.needle4k.db.Person
import org.needle4k.db.TransactionHelper
import java.sql.ResultSet
import javax.annotation.Resource
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.sql.DataSource

@Suppress("CdiInjectionPointsInspection", "SqlNoDataSourceInspection")
class JPAEntityManagerJUnit4Test : AbstractJPAEntityManagerTest(){
  @Rule
  @JvmField
  val needleRule = NeedleRule().withJPAInjection()

  @Inject
  private lateinit var transactionHelper: TransactionHelper

  @Resource
  private lateinit var dataSource: DataSource

  @Test
  fun `test same entity manager`() {
    val entityManager: EntityManager = needleRule.entityManager
    Assertions.assertThat(entityManager).isSameAs(this.entityManager)
  }

  @Test
  override fun `test with real entity manager`() {
    super.`test with real entity manager`()
  }

  @Test
  override fun testTransactions() {
    super.testTransactions()
  }

  @Test
  fun `real data source`() {
    Assertions.assertThat(dataSource).isNotNull
    Assertions.assertThat(Mockito.mockingDetails(dataSource).isMock).isFalse

    transactionHelper.saveObject(Person().apply { myName = "jens" })

    dataSource.connection
      .prepareStatement("SELECT p.id FROM " + Person.TABLE_NAME + " p WHERE p.myName = ?").use {
        it.setString(1, "jens")
        val resultSet: ResultSet = it.executeQuery()

        Assertions.assertThat(resultSet.next()).isTrue
        val id = resultSet.getLong("ID")

        Assertions.assertThat(id).isGreaterThan(0)
      }
  }
}