package org.needle4k.reflection

@Suppress("unused", "RedundantOverride", "UNUSED_PARAMETER")
open class SampleClass {
  @MyAnnotation
  val aPrivateField: String = ""

  @Suppress("unused")
  private val collectionField2: Collection<*> = ArrayList<Any>()

  @Suppress("unused")
  private fun testGetMethod(string: String, value: Int, obj: Any): String {
    return string
  }

  fun testGetPublicDerivedMethod(string: String, value: Int, obj: Any): String {
    return string
  }

  open fun toOverride() {}

  @MyAnnotation
  protected fun aProtectedMethod() {
    // empty
  }
}