package org.needle4k.injection

import org.needle4k.NeedleContext
import org.needle4k.processor.AbstractNeedleProcessor
import org.needle4k.reflection.ReflectionHelper
import org.slf4j.LoggerFactory
import java.lang.reflect.Field

class TestcaseInjectionProcessor(configuration: InjectionConfiguration) : AbstractNeedleProcessor(configuration) {
  override fun process(context: NeedleContext) {
    val supportedAnnotations = configuration.supportedAnnotations

    for (supportedAnnotation in supportedAnnotations) {
      processAnnotation(context, configuration, supportedAnnotation)
    }
  }

  private fun processAnnotation(
    context: NeedleContext, configuration: InjectionConfiguration, annotation: Class<out Annotation>
  ) {
    val fields = context.getAnnotatedTestcaseFields(annotation)

    for (field in fields) {
      processField(context, configuration, field)
    }
  }

  private fun processField(context: NeedleContext, configuration: InjectionConfiguration, field: Field) {
    val needleConfiguration = context.needleConfiguration
    val registry = needleConfiguration.injectionAnnotationRegistry
    val annotation = registry.registeredAnnotation(*field.declaredAnnotations)!!
    val injectionTargetInformation: InjectionTargetInformation<*> = FieldTargetInformation(field, annotation)
    val injection = configuration.handleInjectionProvider(configuration.allInjectionProviders, injectionTargetInformation)

    if (injection != null) {
      val injectedObject = context.getInjectedObject<Any>(injection.first) ?: injection.second

      try {
        ReflectionHelper.setField(field, context.test, injectedObject)
        return
      } catch (e: Exception) {
        LOGGER.error("processField", e)
      }
    }
  }

  companion object {
    private val LOGGER = LoggerFactory.getLogger(TestcaseInjectionProcessor::class.java)
  }
}