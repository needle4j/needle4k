package org.needle4k.mock

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
  fun <T> createMockComponent(type: Class<T>): T
}