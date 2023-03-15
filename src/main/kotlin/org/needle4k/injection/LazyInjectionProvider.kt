package org.needle4k.injection

import java.util.function.Supplier

open class LazyInjectionProvider<T : Any>(private val clazz: Class<T>, private val supplier: Supplier<T>) : InjectionProvider<T> {
  @Suppress("UNCHECKED_CAST")
  override fun <T> getInjectedObject(injectionTargetType: Class<T>): T = supplier.get() as T

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) = clazz

  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
    val type: Class<*> = injectionTargetInformation.injectedObjectType

    return type.isAssignableFrom(clazz)
  }
}