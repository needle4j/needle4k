package org.needle4k.db

import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionTargetInformation
import javax.persistence.EntityManagerFactory

internal class EntityManagerFactoryProvider(private val entityManagerFactory: EntityManagerFactory) :
  InjectionProvider<EntityManagerFactory> {

  @Suppress("UNCHECKED_CAST")
  override fun <T> getInjectedObject(injectionTargetType: Class<T>) = entityManagerFactory as T

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) = EntityManagerFactory::class.java

  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>) =
    injectionTargetInformation.injectedObjectType === EntityManagerFactory::class.java
}