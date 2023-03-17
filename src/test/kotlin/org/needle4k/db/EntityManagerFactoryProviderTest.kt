package org.needle4k.db

import org.junit.Assert
import org.junit.Test
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.FieldTargetInformation
import org.needle4k.reflection.ReflectionHelper
import javax.annotation.Resource
import javax.persistence.EntityManagerFactory

@Suppress("unused")
class EntityManagerFactoryProviderTest {
  private val needleConfiguration = DefaultNeedleConfiguration()
  private val entityManagerFactoryProvider = EntityManagerFactoryProvider(
    JPAInjectorConfiguration(needleConfiguration).entityManagerFactory
  )

  @Resource
  private lateinit var entityManagerFactory: EntityManagerFactory

  @Test
  fun testVerify() {
    val field = ReflectionHelper.getField(this.javaClass, "entityManagerFactory")
    val injectionTargetInformation = FieldTargetInformation(field, field.getAnnotation(Resource::class.java))
    Assert.assertTrue(entityManagerFactoryProvider.verify(injectionTargetInformation))
  }
}