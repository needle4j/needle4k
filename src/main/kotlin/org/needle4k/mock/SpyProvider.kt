package org.needle4k.mock

import java.lang.reflect.Field

/**
 * Interface to abstract the creation of spy() instances, if the used framework
 * allows to do so.
 *
 * @author Jan Galinski, Holisticon AG
 */
interface SpyProvider {
  /**
   * @param instance
   * @return Spy of instance (spy(instance) for Mockito)
   */
  fun <T : Any> createSpyComponent(instance: T): T

  /**
   * @return the Annotation used to trigger the spy creation.
   * (@Spy forMockito)
   */
  val spyAnnotation: Class<out Annotation>?

  fun isSpyRequested(field: Field): Boolean

  fun isSpyPossible(field: Field) = !field.type.isPrimitive

  companion object {
    /**
     * Just return the given instance. Use this as default provider when the
     * mockProvider does not support spies.
     */
    @JvmStatic
    val FAKE: SpyProvider = object : SpyProvider {
      override fun <T : Any> createSpyComponent(instance: T): T {
        return instance
      }

      override val spyAnnotation: Class<out Annotation>
        get() = Deprecated::class.java

      override fun isSpyRequested(field: Field): Boolean {
        return false
      }
    }
  }
}