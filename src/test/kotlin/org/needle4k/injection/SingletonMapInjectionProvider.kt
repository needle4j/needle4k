package org.needle4k.injection

@Suppress("UNCHECKED_CAST")
open class SingletonMapInjectionProvider @JvmOverloads constructor(val map: Map<Any, Any> = HashMap()) :
  InjectionProvider<Map<*, *>> {
  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
    return injectionTargetInformation.injectedObjectType === MutableMap::class.java
  }

  override fun getInjectedObject(injectionTargetType: Class<*>) = map

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) =
    injectionTargetInformation.injectedObjectType
}