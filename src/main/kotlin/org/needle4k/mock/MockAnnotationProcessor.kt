package org.needle4k.mock

import org.needle4k.NeedleContext
import org.needle4k.annotation.Mock
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.processor.AbstractNeedleProcessor
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier

class MockAnnotationProcessor(configuration: InjectionConfiguration) : AbstractNeedleProcessor(configuration) {
  override fun process(context: NeedleContext) {
    val fields = context.getAnnotatedTestcaseFields(Mock::class.java).filter {
      val type = it.type
      val exclude = (Modifier.isFinal(type.modifiers) || type.isPrimitive)

      if (exclude) {
        LOG.debug("Skipping creation of a mock : {} as it is final or primitive type.", type.name)
      }

      !exclude
    }

    for (field in fields) {
      val mock = configuration.mockProvider.createMockComponent(field.type)

      configuration.needleConfiguration.reflectionHelper.setField(field, context.test, mock)
    }
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(MockAnnotationProcessor::class.java)
  }
}