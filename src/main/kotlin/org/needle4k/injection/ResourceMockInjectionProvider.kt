package org.needle4k.injection

import org.needle4k.reflection.ReflectionHelper

class ResourceMockInjectionProvider(
  annotationClass: Class<out Annotation>,
  injectionConfiguration: InjectionConfiguration
) : DefaultMockInjectionProvider<Any>(annotationClass, injectionConfiguration) {
  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any {
    val annotation = injectionTargetInformation.getAnnotation(annotationClass)!!
    val mappedName = ReflectionHelper.invokeMethod(annotation, "mappedName")?.toString()

    return if (mappedName.isNullOrBlank()) {
      super.getKey(injectionTargetInformation)
    } else {
      mappedName
    }
  }
}