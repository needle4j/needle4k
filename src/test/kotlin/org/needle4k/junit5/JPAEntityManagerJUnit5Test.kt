package org.needle4k.junit5

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.needle4k.db.AbstractJPAEntityManagerTest
import javax.inject.Inject

@ExtendWith(value = [JPANeedleExtension::class])
@Disabled
class JPAEntityManagerJUnit5Test : AbstractJPAEntityManagerTest(){
  @Inject
  private lateinit var needle: NeedleExtension

  @Test
  fun `test same entity manager`() {
    val entityManager = needle.jpaInjector?.configuration?.entityManager
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