package org.needle4k.db

import org.needle4k.db.jpa.TransactionHelper
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionTargetInformation

internal class TransactionHelperProvider(private val transactionHelper: TransactionHelper) : InjectionProvider<TransactionHelper> {
  @Suppress("UNCHECKED_CAST")
  override fun <T> getInjectedObject(injectionTargetType: Class<T>): T = transactionHelper as T

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any = TransactionHelper::class.java

  @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
  override fun verify(information: InjectionTargetInformation<*>) =
    information.injectedObjectType === TransactionHelper::class.java
}