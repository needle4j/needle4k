package org.needle4k.mock

import org.mockito.Mockito
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.NeedleConfiguration
import java.lang.reflect.Field

/**
 * A Mockito specific [MockProvider] implementation. For more details, see
 * the Mockito documentation.
 */
class MockitoProvider @JvmOverloads constructor(needleConfiguration: NeedleConfiguration = DefaultNeedleConfiguration.INSTANCE)
  : MockProvider, SpyProvider {
  @Suppress("UNCHECKED_CAST")
  override val spyAnnotation: Class<out Annotation>? = needleConfiguration.reflectionHelper.forName(SPY_ANNOTATION_FQN) as Class<out Annotation>?

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
  override fun <T> createSpyComponent(instance: T): T = Mockito.spy<T>(instance)

  override fun isSpyPossible(field: Field) = super.isSpyPossible(field) && spyAnnotation != null

  override fun isSpyRequested(field: Field) = field.isAnnotationPresent(spyAnnotation)

  companion object {
    const val SPY_ANNOTATION_FQN = "org.mockito.Spy"
  }
}