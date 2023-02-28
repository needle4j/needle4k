package org.needle4k.configuration

import org.needle4k.reflection.ReflectionUtil
import org.needle4k.registries.AnnotationRegistry

class NeedleConfiguration {
  val annotationRegistry = AnnotationRegistry(this)
  val reflectionHelper = ReflectionUtil(this)
}
