package org.needle4k.injection

import org.needle4k.NeedleContext
import org.needle4k.annotation.InjectInto
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.processor.NeedleProcessor
import org.slf4j.LoggerFactory
import java.lang.reflect.Field

class InjectionAnnotationProcessor : NeedleProcessor {
  override fun process(context: NeedleContext) {
    proccessInjectIntoMany(context)
    proccessInjectInto(context)
  }

  private fun proccessInjectIntoMany(context: NeedleContext) {
    val reflectionUtil = context.needleConfiguration.reflectionHelper
    val testcase: Any = context.test
    val fieldsWithInjectIntoManyAnnotation: List<Field> = context.getAnnotatedTestcaseFields(
      InjectIntoMany::class.java
    )
    for (field in fieldsWithInjectIntoManyAnnotation) {
      val sourceObject: Any = reflectionUtil.getFieldValue(testcase, field)!!
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
    }
  }

  private fun proccessInjectInto(context: NeedleContext) {
    val reflectionUtil = context.needleConfiguration.reflectionHelper
    val testcase: Any = context.test
    val fields: List<Field> = context.getAnnotatedTestcaseFields(InjectInto::class.java)

    for (field in fields) {
      val sourceObject: Any = reflectionUtil.getFieldValue(testcase, field)!!
      processInjectInto(context, field, sourceObject, field.getAnnotation(InjectInto::class.java))
    }
  }

  private fun processInjectInto(
    context: NeedleContext, field: Field, sourceObject: Any,
    injectInto: InjectInto
  ) {
    val instance = context.getObjectUnderTest(injectInto.targetComponentId)

    if (instance != null) {
      if (injectInto.fieldName.isBlank()) {
        injectByType(context, instance, sourceObject, field.type)
      } else {
        injectByFieldName(context, instance, sourceObject, injectInto.fieldName)
      }
    } else {
      LOGGER.warn(
        "Could not inject component {} -  unknown object under test with id {}", sourceObject, injectInto.targetComponentId
      )
    }
  }

  private fun injectByType(context: NeedleContext, objectUnderTest: Any, sourceObject: Any, type: Class<*>) {
    val needleConfiguration = context.needleConfiguration
    val registry = needleConfiguration.injectionAnnotationRegistry
    val reflectionUtil = needleConfiguration.reflectionHelper
    val fields: List<Field> = reflectionUtil.getAllFieldsAssignableFrom(type, objectUnderTest.javaClass)
      .filter { registry.isRegistered(*it.declaredAnnotations) }

    for (field in fields) {
      try {
        reflectionUtil.setField(field, objectUnderTest, sourceObject)
      } catch (e: Exception) {
        LOGGER.warn("could not inject into component $objectUnderTest", e)
      }
    }
  }

  private fun injectByFieldName(context: NeedleContext,objectUnderTest: Any, sourceObject: Any, fieldName: String) {
    val reflectionUtil = context.needleConfiguration.reflectionHelper

    try {
      reflectionUtil.setField(fieldName, objectUnderTest, sourceObject)
    } catch (e: Exception) {
      LOGGER.warn("could not inject into component $objectUnderTest", e)
    }
  }

  companion object {
    private val LOGGER = LoggerFactory.getLogger(InjectionAnnotationProcessor::class.java)
  }
}