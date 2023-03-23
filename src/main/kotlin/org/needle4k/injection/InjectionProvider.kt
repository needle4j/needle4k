package org.needle4k.injection

/**
 * Provides instances of `T` and verifies the injection target.
 */
interface InjectionProvider<T> {
  /**
   * Provides an instance of `T`.
   *
   * @param injectionTargetType the type of the injection target.
   * @return instance of `T`
   */
  fun getInjectedObject(injectionTargetType: Class<*>): T?

  /**
   * Returns a key object, which identifies the provided object.
   *
   * @param injectionTargetInformation information about the injection point
   * @return the key of the provided object
   */
  fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any

  /**
   * Verifies the injection target.
   *
   * @param injectionTargetInformation information about the injection point
   * @return true, if the provided object is injectable to the given injection
   * information, otherwise false.
   */
  fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean
}