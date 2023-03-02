package org.needle4k.mock

import org.mockito.Mockito
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import org.needle4k.configuration.NeedleConfiguration

/**
 * A Mockito specific [MockProvider] implementation. For more details, see
 * the Mockito documentation.
 */
class MockitoProvider(needleConfiguration: NeedleConfiguration) : MockProvider, SpyProvider {
  @Suppress("UNCHECKED_CAST")
  override val spyAnnotation: Class<out Annotation>? = needleConfiguration.reflectionHelper.forName(SPY_ANNOTATION_FQN) as Class<out Annotation>?

  /**
   * {@inheritDoc} Skipping creation, if the type is final or primitive.
   *
   * @return the mock object or null, if the type is final or primitive.
   */
  override fun <T> createMockComponent(type: Class<T>): T = Mockito.mock<T>(type)

  /**
   * {@inheritDoc} Skipping creation, if the type is final or primitive.
   *
   * @return the mock object or null, if the type is final or primitive.
   */
  override fun <T> createSpyComponent(instance: T): T = Mockito.spy<T>(instance)

  override fun isSpyPossible(field: Field) = super.isSpyPossible(field) && spyAnnotation != null

  override fun isSpyRequested(field: Field) = field.isAnnotationPresent(spyAnnotation)

  companion object {
    private val LOG = LoggerFactory.getLogger(MockitoProvider::class.java)

    const val SPY_ANNOTATION_FQN = "org.mockito.Spy"
  }
}