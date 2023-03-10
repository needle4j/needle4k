package org.needle4k.injection

@Suppress("UNCHECKED_CAST")
open class CustomMapInjectionProvider : InjectionProvider<Map<Any, Any>> {
  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
    return injectionTargetInformation.injectedObjectType === MutableMap::class.java
  }

  override fun <T> getInjectedObject(injectionTargetType: Class<T>): T = MAP as T

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any {
    return injectionTargetInformation.injectedObjectType
  }

  companion object {
    val MAP: Map<Any, Any> = HashMap()
  }
}