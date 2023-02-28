package org.needle4k.registries

import org.needle4k.configuration.NeedleConfiguration
import kotlin.collections.HashSet

class AnnotationRegistry(private val configuration: NeedleConfiguration) {
  private val registeredAnnotations = HashSet<Class<out Annotation>>()

  fun allAnnotations() = registeredAnnotations.toList()

  fun addAnnotation(className: String) {
    val clazz = configuration.reflectionHelper.lookupClass(Annotation::class.java, className)

    if (clazz != null) {
      registeredAnnotations.add(clazz)
    }
  }

  fun addAnnotation(annotationClass: Class<out Annotation>) {
    registeredAnnotations.add(annotationClass)
  }

  fun removeAnnotation(annotationClass: Class<out Annotation>) {
    registeredAnnotations.remove(annotationClass)
  }

  fun isRegistered(vararg annotations: Annotation): Boolean {
    val annotationClasses = annotations.map { it.annotationClass.java }.toSet()

    return registeredAnnotations.intersect(annotationClasses).isNotEmpty()
  }
}