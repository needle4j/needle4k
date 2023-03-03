package org.needle4k

import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.configuration.NeedleConfiguration
import java.lang.reflect.Field

class NeedleContext(val test: Any, val needleConfiguration: NeedleConfiguration) {
  private val objectsUnderTest: MutableMap<String, Any> = HashMap()
  private val objectUnderTestAnnotations = HashMap<String, ObjectUnderTest>()
  private val injectedObjects: MutableMap<Any, Any> = HashMap()
  private val annotatedTestcaseFieldMap = needleConfiguration.reflectionHelper.getAllAnnotatedFields(test.javaClass)

  @Suppress("UNCHECKED_CAST")
  fun <X> getInjectedObject(key: Any): X? {
    return injectedObjects[key] as X?
  }

  fun getInjectedObjects(): Collection<Any> {
    return injectedObjects.values
  }

  fun addInjectedObject(key: Any, instance: Any) {
    injectedObjects[key] = instance
  }

  fun getObjectUnderTest(id: String): Any {
    return objectsUnderTest[id]!!
  }

  fun getObjectUnderTestAnnotation(id: String): ObjectUnderTest? {
    return objectUnderTestAnnotations[id]
  }

  fun addObjectUnderTest(
    id: String, instance: Any,
    objectUnderTestAnnotation: ObjectUnderTest
  ) {
    objectsUnderTest[id] = instance
    objectUnderTestAnnotations[id] = objectUnderTestAnnotation
  }

  fun getObjectsUnderTest(): Collection<Any> {
    return objectsUnderTest.values
  }

  val objectsUnderTestIds: Set<String>
    get() = objectsUnderTest.keys

  fun getAnnotatedTestcaseFields(annotationClass: Class<out Annotation>): List<Field> =
    annotatedTestcaseFieldMap[annotationClass] ?: ArrayList()
}