package org.needle4k.mock

import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier

/**
 * Interface for abstraction of a specific mock provider.
 */
interface MockProvider {
  /**
   * Creates a mock object of a given class or interface.
   *
   * @param type class or interface to mock
   * @return mock object
   */
  fun <T> doCreateMockComponent(type: Class<T>): T?

  fun <T> createMockComponent(type: Class<T>): T? =
    if (checkComponent(type)) doCreateMockComponent(type) else null

  fun <T> checkComponent(type: Class<T>): Boolean {
    return if (Modifier.isFinal(type.modifiers) || type.isPrimitive) {
      LOG.warn("Skipping creation of a mock : {} as it is final or primitive type.", type.simpleName)
      false
    } else {
      true
    }
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(MockProvider::class.java)
  }
}