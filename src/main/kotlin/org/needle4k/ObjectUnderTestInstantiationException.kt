package org.needle4k

/**
 * Thrown when an [org.needle4k.annotation.ObjectUnderTest] instantiation fails, e.g. if the
 * associated class object has no corresponding constructor or the class is
 * abstract, a primitive type or an interface.
 */
class ObjectUnderTestInstantiationException : Exception {
  constructor(message: String, cause: Throwable) : super(message, cause)
  constructor(message: String) : super(message)
  constructor(cause: Throwable) : super(cause)

  companion object {
    private const val serialVersionUID = 1L
  }
}