package org.needle4k.mock

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.Mock
import org.needle4k.junit4.NeedleRule
import javax.persistence.EntityManager

class MockAnnotationTest {
  @Rule
  @JvmField
  val needleRule = NeedleRule()

  @Mock
  private lateinit var entityManagerMock: EntityManager

  @Test
  fun testMockAnnotation() {
    Assert.assertNotNull(entityManagerMock)
  }
}