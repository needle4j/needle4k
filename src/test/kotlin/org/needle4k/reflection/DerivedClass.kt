package org.needle4k.reflection

@Suppress("unused", "RedundantOverride", "UNUSED_PARAMETER", "PLATFORM_CLASS_MAPPED_TO_KOTLIN", "RemoveRedundantQualifierName")
class DerivedClass : SampleClass() {
  private val field: String = ""
  val booleanField: Boolean = false
  private val collectionField2: Collection<*> = ArrayList<Any>()

  override fun toOverride() {
    super.toOverride()
  }

  private fun testInvokeWithPrimitive(
    intValue: Int, floatValue: Float, charValue: Char,
    booleanValue: Boolean, longValue: Long, byteValue: Byte, shortValue: Short,
    doubleValue: Double
  ): Boolean {
    return true
  }

  private fun testInvokeWithObjects(
    intValue: java.lang.Integer, floatValue: java.lang.Float, charValue: java.lang.Character,
    booleanValue: java.lang.Boolean, longValue: java.lang.Long, byteValue: java.lang.Byte, shortValue: java.lang.Short,
    doubleValue: java.lang.Double
  ): Boolean {
    return true
  }

  @MyAnnotation
  private fun aPrivateMethod() {
    // empty
  }
}