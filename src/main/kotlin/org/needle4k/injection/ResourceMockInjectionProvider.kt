package org.needle4k.injection

class ResourceMockInjectionProvider(
  annotationClass: Class<out Annotation>,
  injectionConfiguration: InjectionConfiguration
) : DefaultMockInjectionProvider<Any>(annotationClass, injectionConfiguration) {
  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any {
    val annotation = injectionTargetInformation.getAnnotation(annotationClass)!!
    val reflectionHelper = injectionConfiguration.needleConfiguration.reflectionUtil
    val mappedName = reflectionHelper.invokeMethod(annotation, "mappedName")?.toString()

    return if (mappedName.isNullOrBlank()) {
      super.getKey(injectionTargetInformation)
    } else {
      mappedName
    }
  }
}