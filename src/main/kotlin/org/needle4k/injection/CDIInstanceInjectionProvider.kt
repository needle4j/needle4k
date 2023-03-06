package org.needle4k.injection

// TODO: How can this work??
class CDIInstanceInjectionProvider(private val instanceClass : Class<*>, private val injectionConfiguration: InjectionConfiguration)
  : InjectionProvider<Any> {
  override fun <T> getInjectedObject(injectionTargetType: Class<T>): T? =
    injectionConfiguration.mockProvider.createMockComponent(injectionTargetType)

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) = injectionTargetInformation.parameterizedType

  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>) =
    injectionTargetInformation.injectedObjectType == instanceClass
}