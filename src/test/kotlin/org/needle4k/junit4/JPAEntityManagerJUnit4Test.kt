package org.needle4k.junit4

import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.needle4k.db.AbstractJPAEntityManagerTest
import javax.persistence.EntityManager

class JPAEntityManagerJUnit4Test : AbstractJPAEntityManagerTest(){
  @Rule
  @JvmField
  val needleRule = NeedleRule().withJPAInjection()

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
}