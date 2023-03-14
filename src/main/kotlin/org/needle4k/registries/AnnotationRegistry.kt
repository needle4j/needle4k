package org.needle4k.registries

import org.needle4k.configuration.NeedleConfiguration

class AnnotationRegistry(private val configuration: NeedleConfiguration) {
  private val registeredAnnotations = HashSet<Class<out Annotation>>()

  fun allAnnotations() = registeredAnnotations.toSet()

  fun addAnnotation(className: String): AnnotationRegistry {
    val clazz = configuration.reflectionUtil.lookupClass(Annotation::class.java, className)

    if (clazz != null) {
      registeredAnnotations.add(clazz)
    }

    return this
  }

  fun addAnnotation(annotationClass: Class<out Annotation>): AnnotationRegistry {
    registeredAnnotations.add(annotationClass)

    return this
  }

  fun removeAnnotation(annotationClass: Class<out Annotation>) {
    registeredAnnotations.remove(annotationClass)
  }

  fun isRegistered(vararg annotations: Annotation) = registeredAnnotation(*annotations) != null

  fun registeredAnnotation(vararg annotations: Annotation): Annotation? = annotations.associateBy { it.annotationClass.java }
    .filterKeys { registeredAnnotations.contains(it) }.values.firstOrNull()

  fun isRegistered(vararg annotationClasses: Class<out Annotation>) =
    annotationClasses.toSet().intersect(registeredAnnotations).isNotEmpty()

  fun isRegistered(vararg classNames: String) =
    classNames.mapNotNull { configuration.reflectionUtil.lookupClass(Annotation::class.java, it) }.toSet()
      .intersect(registeredAnnotations).isNotEmpty()
}