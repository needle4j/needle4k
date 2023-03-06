package org.needle4k.db

import org.junit.Assert
import org.junit.Test
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.FieldTargetInformation
import javax.annotation.Resource
import javax.persistence.EntityManagerFactory

@Suppress("unused")
class EntityManagerFactoryProviderTest {
  private val needleConfiguration = DefaultNeedleConfiguration.INSTANCE
  private val entityManagerFactoryProvider = EntityManagerFactoryProvider(
    DatabaseInjectorConfiguration(needleConfiguration).entityManagerFactory
  )

  @Resource
  private lateinit var entityManagerFactory: EntityManagerFactory

  @Test
  fun testVerify() {
    val field = needleConfiguration.reflectionHelper.getField(this.javaClass, "entityManagerFactory")
    val injectionTargetInformation = FieldTargetInformation(field, field.getAnnotation(Resource::class.java))
    Assert.assertTrue(entityManagerFactoryProvider.verify(injectionTargetInformation))
  }
}