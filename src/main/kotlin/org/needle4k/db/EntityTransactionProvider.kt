package org.needle4k.db

import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionTargetInformation
import javax.persistence.EntityManager
import javax.persistence.EntityTransaction

internal class EntityTransactionProvider(private val entityManager: EntityManager) : InjectionProvider<EntityTransaction> {
  override fun getInjectedObject(injectionTargetType: Class<*>): EntityTransaction = entityManager.transaction

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) = EntityTransaction::class.java

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
  override fun verify(information: InjectionTargetInformation<*>) =
    information.injectedObjectType == EntityTransaction::class.java
}