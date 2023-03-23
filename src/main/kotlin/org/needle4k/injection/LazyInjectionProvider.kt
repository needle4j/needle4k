package org.needle4k.injection

import java.util.function.Supplier

open class LazyInjectionProvider<T>(private val clazz: Class<T>, private val supplier: Supplier<T>) : InjectionProvider<T> {
  @Suppress("UNCHECKED_CAST")
  override fun getInjectedObject(injectionTargetType: Class<*>) = supplier.get()

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) = clazz

  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
    val type: Class<*> = injectionTargetInformation.injectedObjectType

    return type.isAssignableFrom(clazz)
  }
}