package org.needle4k.injection

import org.needle4k.NeedleContext
import org.needle4k.annotation.InjectInto
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.processor.NeedleProcessor
import org.needle4k.reflection.ReflectionUtil
import org.slf4j.LoggerFactory
import java.lang.reflect.Field

class InjectionAnnotationProcessor : NeedleProcessor {
  override fun process(context: NeedleContext) {
    processInjectIntoMany(context)
    processInjectInto(context)
  }

  private fun processInjectIntoMany(context: NeedleContext) {
    val testcase: Any = context.test
    val fieldsWithInjectIntoManyAnnotation: List<Field> = context.getAnnotatedTestcaseFields(
      InjectIntoMany::class.java
    )
    for (field in fieldsWithInjectIntoManyAnnotation) {
      val sourceObject = ReflectionUtil.getFieldValue(testcase, field)

      if (sourceObject != null) {
        val injectIntoManyAnnotation: InjectIntoMany = field.getAnnotation(InjectIntoMany::class.java)
        val value = injectIntoManyAnnotation.value

        // inject into all object under test instance
        if (value.isEmpty()) {
          for (objectUnderTest in context.objectsUnderTest()) {
            injectByType(context, objectUnderTest, sourceObject, field.type)
          }
        } else {
          for (injectInto in value) {
            processInjectInto(context, field, sourceObject, injectInto)
          }
        }
      } else {
        LOG.warn("@InjectIntoMany field $field has null value and thus will be ignored.")
      }
    }
  }

  private fun processInjectInto(context: NeedleContext) {
    val testcase: Any = context.test
    val fields: List<Field> = context.getAnnotatedTestcaseFields(InjectInto::class.java)

    for (field in fields) {
      val sourceObject = ReflectionUtil.getFieldValue(testcase, field)

      if (sourceObject != null) {
        processInjectInto(context, field, sourceObject, field.getAnnotation(InjectInto::class.java))
      } else {
        LOG.warn("@InjectInto field $field has null value and thus will be ignored.")
      }
    }
  }

  private fun processInjectInto(context: NeedleContext, field: Field, sourceObject: Any, injectInto: InjectInto) {
    val instance = context.getObjectUnderTest(injectInto.targetComponentId)

    if (instance != null) {
      if (injectInto.fieldName.isBlank()) {
        injectByType(context, instance, sourceObject, field.type)
      } else {
        injectByFieldName(instance, sourceObject, injectInto.fieldName)
      }
    } else {
      LOG.warn(
        "Could not inject component {} -  unknown object under test with id {}", sourceObject, injectInto.targetComponentId
      )
    }
  }

  private fun injectByType(context: NeedleContext, objectUnderTest: Any, sourceObject: Any, type: Class<*>) {
    val needleConfiguration = context.needleConfiguration
    val registry = needleConfiguration.injectionAnnotationRegistry
    val fields = ReflectionUtil.getAllFieldsAssignableFrom(type, objectUnderTest.javaClass)
      .filter { registry.isRegistered(*it.declaredAnnotations) }

    for (field in fields) {
      try {
        ReflectionUtil.setField(field, objectUnderTest, sourceObject)
      } catch (e: Exception) {
        LOG.warn("could not inject into component $objectUnderTest", e)
      }
    }
  }

  private fun injectByFieldName(objectUnderTest: Any, sourceObject: Any, fieldName: String) {
    try {
      ReflectionUtil.setField(fieldName, objectUnderTest, sourceObject)
    } catch (e: Exception) {
      LOG.warn("could not inject into component $objectUnderTest", e)
    }
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(InjectionAnnotationProcessor::class.java)
  }
}