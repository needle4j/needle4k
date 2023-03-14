package org.needle4k.mock

import org.mockito.Mockito
import org.needle4k.configuration.NeedleConfiguration
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * A Mockito specific [MockProvider] implementation. For more details, see
 * the Mockito documentation.
 */
class MockitoProvider(needleConfiguration: NeedleConfiguration) :
  MockProvider, SpyProvider {
  @Suppress("UNCHECKED_CAST")
  override val spyAnnotation: Class<out Annotation>? =
    needleConfiguration.reflectionHelper.forName(SPY_ANNOTATION_FQN) as Class<out Annotation>?

  /**
   * {@inheritDoc} Skipping creation, if the type is final or primitive.
   *
   * @return the mock object or null, if the type is final or primitive.
   */
  override fun <T> doCreateMockComponent(type: Class<T>): T = Mockito.mock(type)

  /**
   * {@inheritDoc} Skipping creation, if the type is final or primitive.
   *
   * @return the mock object or null, if the type is final or primitive.
   */
  override fun <T : Any> createSpyComponent(instance: T): T {
    val type: Class<*> = instance.javaClass

    return if (Modifier.isFinal(type.modifiers) || type.isPrimitive) {
      LOG.debug("Skipping creation of a spy : {} as it is final or primitive type.", type.name)

      instance
    } else {
      Mockito.spy(instance)
    }
  }

  override fun isSpyPossible(field: Field) = super.isSpyPossible(field) && spyAnnotation != null

  override fun isSpyRequested(field: Field) = field.isAnnotationPresent(spyAnnotation)

  override fun reset() {}

  companion object {
    const val SPY_ANNOTATION_FQN = "org.mockito.Spy"

    val LOG = LoggerFactory.getLogger(MockitoProvider::class.java)!!
  }
}