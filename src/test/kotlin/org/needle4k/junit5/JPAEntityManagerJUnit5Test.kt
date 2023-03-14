package org.needle4k.junit5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.needle4k.db.AbstractJPAEntityManagerTest
import org.needle4k.db.JPAInjector
import javax.inject.Inject

@ExtendWith(value = [JPANeedleExtension::class])
class JPAEntityManagerJUnit5Test : AbstractJPAEntityManagerTest(){
  @Inject
  private lateinit var needle: JPANeedleExtension

  @Inject
  private lateinit var jpaInjector: JPAInjector

//  @BeforeEach
//  fun init() {
//    needle.withJPAInjection()
//  }

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
    assertThat(needle.jpaInjector.configuration.entityManager).isSameAs(this.entityManager)
    assertThat(needle.jpaInjector).isSameAs(this.jpaInjector)
  }
}