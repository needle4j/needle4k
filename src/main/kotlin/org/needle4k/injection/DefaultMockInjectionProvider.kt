package org.needle4k.injection

@Suppress("UNCHECKED_CAST")
open class DefaultMockInjectionProvider<T>
/**
 * @param annotationClass        injection annotation like Resource, EJB, Inject, ...
 * @param injectionConfiguration the configuration
 */(
  private val annotationClass: Class<out Annotation>,
  private val injectionConfiguration: InjectionConfiguration
) : InjectionProvider<T> {
  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>) =
    injectionTargetInformation.injectionAnnotation.javaClass === annotationClass
        || annotationClass.isAnnotation && injectionTargetInformation.isAnnotationPresent(annotationClass)

  override fun <T> getInjectedObject(injectionTargetType: Class<T>): T =
    injectionConfiguration.mockProvider.createMockComponent(injectionTargetType)

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any =
    injectionTargetInformation.injectedObjectType
}