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
}

class ConstructorTargetInformation(constructor: Constructor<*>, parameter: Parameter, injectionAnnotation: Annotation) :
    ExecutableTargetInformation<Constructor<*>>(constructor, parameter, injectionAnnotation)

class MethodTargetInformation(method: Method, parameter: Parameter, injectionAnnotation: Annotation) :
    ExecutableTargetInformation<Method>(method, parameter, injectionAnnotation)
