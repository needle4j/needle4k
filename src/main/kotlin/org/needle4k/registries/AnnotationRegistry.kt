package org.needle4k.registries

import org.needle4k.configuration.NeedleConfiguration
import java.util.*

class AnnotationRegistry(val configuration: NeedleConfiguration) {
  private val registeredAnnotations = TreeSet<Class<out Annotation>>()

  fun allAnnotations() = registeredAnnotations.toList()

  fun addAnnotation(className: String) {

  }

  fun addAnnotation(annotationClass: Class<out Annotation>) {
    registeredAnnotations.add(annotationClass)
  }

  fun removeAnnotation(annotationClass: Class<out Annotation>) {
    registeredAnnotations.remove(annotationClass)
  }

  fun isRegistered(vararg annotations: Annotation): Boolean {
    val annotationClasses = annotations.map { it.javaClass }.toSet()

    return registeredAnnotations.intersect(annotationClasses).isNotEmpty()
  }
}