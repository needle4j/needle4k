package org.needle4k.injection

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.inject.Named

class AnnotationsTest {
  @Test
  fun shouldReturnTrueForValidQualifier() {
    assertTrue(CurrentUser::class.java.isQualifier())
    assertTrue(Named::class.java.isQualifier())
    assertFalse(Test::class.java.isQualifier())
  }
}