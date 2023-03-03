package org.needle4k.injection

import javax.annotation.Resource

class ResourceMockInjectionProvider(injectionConfiguration: InjectionConfiguration) : DefaultMockInjectionProvider<Any>(
  Resource::class.java, injectionConfiguration
) {
  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any {
    val annotation = injectionTargetInformation.getAnnotation(Resource::class.java)

    return if (annotation != null && annotation.mappedName.isNotBlank()) {
      annotation.mappedName
    } else super.getKey(injectionTargetInformation)
  }
}