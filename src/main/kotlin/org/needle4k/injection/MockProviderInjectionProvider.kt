package org.needle4k.injection

import org.needle4k.mock.MockProvider

class MockProviderInjectionProvider(private val mockProvider: MockProvider) : InjectionProvider<MockProvider> {
  @Suppress("UNCHECKED_CAST")
  override fun <T> getInjectedObject(injectionTargetType: Class<T>): T = mockProvider as T


  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any = MockProvider::class.java

  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
    val type: Class<*> = injectionTargetInformation.injectedObjectType

    return type.isAssignableFrom(mockProvider.javaClass)
  }
}