package org.needle4k.injection

import java.lang.reflect.*

/**
 * Provides information about the injection target.
 */
sealed class InjectionTargetInformation<T : AccessibleObject>(val fieldOrMethod: T) {
  /**
   * Type of the object to be injected.
   */
  abstract val injectedObjectType: Class<*>

  /**
   * Generic type parameter of type, if any
   */
  abstract val parameterizedType: Type

  /**
   * Injection annotation of this injection target
   */
  abstract val injectionAnnotation: Annotation


  /**
   * Returns true if an annotation for the specified type is present on the
   * injection target, else false.
   *
   * @param annotationClass - the Class object corresponding to the annotation type
   * @return true if an annotation for the specified annotation type is
   * present on this element, else false
   * @throws NullPointerException - if the given annotation class is null
   */
  fun isAnnotationPresent(annotationClass: Class<out Annotation>) = getAnnotation(annotationClass) != null

  /**
   * Returns the [Annotation] object if an annotation for the specified
   * type is present on the injection target, otherwise null.
   *
   * If the [AccessibleObject] of the injection target is of type
   * [Method] or [Constructor], then the [Annotation] may be
   * specified on the [AccessibleObject] or on the corresponding
   * parameter.
   *
   * @param annotationClass - the Class object corresponding to the annotation type
   * @return annotation for the specified annotation type if present on this
   * element, otherwise null
   * @throws NullPointerException - if the given annotation class is null
   */
  @Suppress("UNCHECKED_CAST")
  open fun <T:Annotation> getAnnotation(annotationClass: Class<T>): T? = fieldOrMethod.getAnnotation(annotationClass)
}

class FieldTargetInformation(field: Field, override val injectionAnnotation: Annotation) :
  InjectionTargetInformation<Field>(field) {
  override val injectedObjectType: Class<*>
    get() = fieldOrMethod.type

  override val parameterizedType: Type
    get() = fieldOrMethod.genericType
}

@Suppress("MemberVisibilityCanBePrivate")
sealed class ExecutableTargetInformation<T : Executable>(
  executable: T,
  val parameter: Parameter,
  override val injectionAnnotation: Annotation
) : InjectionTargetInformation<T>(executable) {
  override val injectedObjectType: Class<*>
    get() = parameter.type

  override val parameterizedType: Type
    get() = parameter.parameterizedType

  @Suppress("UNCHECKED_CAST")
  override fun <T:Annotation> getAnnotation(annotationClass: Class<T>): T? =
    super.getAnnotation(annotationClass) ?: parameter.getAnnotation(annotationClass)
}

class ConstructorTargetInformation(constructor: Constructor<*>, parameter: Parameter, injectionAnnotation: Annotation) :
  ExecutableTargetInformation<Constructor<*>>(constructor, parameter, injectionAnnotation)

class MethodTargetInformation(method: Method, parameter: Parameter, injectionAnnotation: Annotation) :
  ExecutableTargetInformation<Method>(method, parameter, injectionAnnotation)
