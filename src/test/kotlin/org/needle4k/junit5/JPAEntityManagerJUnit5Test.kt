package org.needle4k.junit5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.needle4k.db.AbstractJPAEntityManagerTest
import org.needle4k.db.JPAInjectionProvider
import javax.inject.Inject

@Suppress("CdiInjectionPointsInspection")
@ExtendWith(JPANeedleExtension::class)
class JPAEntityManagerJUnit5Test : AbstractJPAEntityManagerTest(){
  @Inject
  private lateinit var needle: JPANeedleExtension

  @Inject
  private lateinit var jpaInjectionProvider: JPAInjectionProvider

  @Test
  override fun `test with real entity manager`() {
    super.`test with real entity manager`()
  }

  @Test
  override fun testTransactions() {
    super.testTransactions()
  }

  @Test
  fun `test same entity manager`() {
    assertThat(needle.jpaInjectionProvider.configuration.entityManager).isSameAs(this.entityManager)
    assertThat(needle.jpaInjectionProvider).isSameAs(this.jpaInjectionProvider)
  }
}