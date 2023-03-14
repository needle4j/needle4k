package org.needle4k.db

import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject
import javax.persistence.EntityManager

class MockEntityManagerTest {
  @Rule
  @JvmField
  val needleRule = NeedleRule()

  @Inject
  private lateinit var entityManager: EntityManager

  @Test
  fun `Entity manager is a mock`() {
    Assertions.assertThat(Mockito.mockingDetails(entityManager).isMock).isTrue
  }
}