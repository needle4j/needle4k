package org.needle4k.reflection

@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
  AnnotationTarget.FIELD
)
@Retention(
  AnnotationRetention.RUNTIME
)
annotation class MyAnnotation 