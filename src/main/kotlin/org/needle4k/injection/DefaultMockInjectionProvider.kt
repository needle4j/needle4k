package org.needle4k.injection

@Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
open class DefaultMockInjectionProvider<T>
/**
 * @param annotationClass        injection annotation like Resource, EJB, Inject, ...
 * @param injectionConfiguration the configuration
 */(
  protected val annotationClass: Class<out Annotation>,
  protected val injectionConfiguration: InjectionConfiguration
) : InjectionProvider<T> {
  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>) =
    injectionTargetInformation.injectionAnnotation.javaClass === annotationClass
        || annotationClass.isAnnotation && injectionTargetInformation.isAnnotationPresent(annotationClass)

  override fun getInjectedObject(injectionTargetType: Class<*>): T =
    injectionConfiguration.mockProvider.createMockComponent(injectionTargetType) as T

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any =
    injectionTargetInformation.injectedObjectType
}