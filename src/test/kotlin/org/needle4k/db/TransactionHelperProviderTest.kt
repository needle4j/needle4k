package org.needle4k.db

import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.FieldTargetInformation
import org.needle4k.reflection.ReflectionHelper
import javax.inject.Inject
import javax.persistence.EntityManager

@Suppress("unused")
class TransactionHelperProviderTest {
  private val configuration = DefaultNeedleConfiguration()
  private val provider = TransactionHelperProvider(TransactionHelper(Mockito.mock(EntityManager::class.java)))

  @Inject
  private lateinit var helper: TransactionHelper

  @Test
  fun testVerify() {
    val field = ReflectionHelper.getField(this.javaClass, "helper")
    val injectionTargetInformation = FieldTargetInformation(field, field.getAnnotation(Inject::class.java))

    Assert.assertTrue(provider.verify(injectionTargetInformation))
    Assert.assertNotNull(provider.getInjectedObject(TransactionHelper::class.java))
  }
}