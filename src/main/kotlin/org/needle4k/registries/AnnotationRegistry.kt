package org.needle4k.registries

import org.needle4k.configuration.NeedleConfiguration

class AnnotationRegistry(private val configuration: NeedleConfiguration) {
  private val registeredAnnotations = HashSet<Class<out Annotation>>()

  fun allAnnotations() = registeredAnnotations.toList()

  fun addAnnotation(className: String): AnnotationRegistry {
    val clazz = configuration.reflectionHelper.lookupClass(Annotation::class.java, className)

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

  fun isRegistered(vararg annotations: Annotation): Boolean {
    val annotationClasses = annotations.map { it.annotationClass.java }.toSet()

    return registeredAnnotations.intersect(annotationClasses).isNotEmpty()
  }

  fun isRegistered(vararg classNames: String): Boolean {
    val annotationClasses =
      classNames.mapNotNull { configuration.reflectionHelper.lookupClass(Annotation::class.java, it) }.toSet()
    return registeredAnnotations.intersect(annotationClasses).isNotEmpty()
  }
}