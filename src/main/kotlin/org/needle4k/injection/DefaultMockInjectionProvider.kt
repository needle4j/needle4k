package org.needle4k.injection

@Suppress("UNCHECKED_CAST")
class DefaultMockInjectionProvider<T>
/**
 * @param annotationClass        injection annotation like Resource, EJB, Inject, ...
 * @param injectionConfiguration the configuration
 */(
  private val annotationClass: Class<T>,
  private val injectionConfiguration: InjectionConfiguration
) : InjectionProvider<T> {


  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
    return injectionTargetInformation.injectedObjectType === annotationClass || annotationClass.isAnnotation && injectionTargetInformation
      .isAnnotationPresent(annotationClass as Class<out Annotation>)
  }

  override fun <T : Any> getInjectedObject(injectionTargetType: Class<T>): T =
    injectionConfiguration.mockProvider.createMockComponent(annotationClass) as T

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) = injectionTargetInformation.injectedObjectType
}