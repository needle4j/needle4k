package org.needle4k

import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.reflection.ReflectionUtil
import java.lang.reflect.Field

class NeedleContext(val test: Any, val needleConfiguration: NeedleConfiguration) {
  private val objectsUnderTest: MutableMap<String, Any> = HashMap()
  private val objectUnderTestAnnotations = HashMap<String, ObjectUnderTest>()
  private val injectedObjects: MutableMap<Any, Any?> = HashMap()
  private val annotatedTestcaseFieldMap = ReflectionUtil.getAllAnnotatedFields(test.javaClass)

  @Suppress("UNCHECKED_CAST")
  fun <X> getInjectedObject(key: Any): X? = injectedObjects[key] as X?

  fun addInjectedObject(key: Any, instance: Any?) {
    injectedObjects[key] = instance
  }

  fun getObjectUnderTest(id: String) = objectsUnderTest[id]

  fun getObjectUnderTestAnnotation(id: String): ObjectUnderTest? = objectUnderTestAnnotations[id]

  fun addObjectUnderTest(id: String, instance: Any, objectUnderTestAnnotation: ObjectUnderTest) {
    objectsUnderTest[id] = instance
    objectUnderTestAnnotations[id] = objectUnderTestAnnotation
  }

  fun objectsUnderTest() = objectsUnderTest.values.toList()

  val objectsUnderTestIds: Set<String> get() = objectsUnderTest.keys.toSet()

  fun getAnnotatedTestcaseFields(annotationClass: Class<out Annotation>): List<Field> =
    annotatedTestcaseFieldMap[annotationClass] ?: ArrayList()
}