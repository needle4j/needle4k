package org.needle4k.db

import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionTargetInformation
import javax.persistence.EntityManager

internal class EntityManagerProvider(private val entityManager: EntityManager) : InjectionProvider<EntityManager> {
  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>) =
    injectionTargetInformation.injectedObjectType === EntityManager::class.java

  override fun getInjectedObject(injectionTargetType: Class<*>) = entityManager

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) = EntityManager::class.java
}