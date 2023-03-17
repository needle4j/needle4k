package org.needle4k

import org.junit.Test
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.InjectionConfiguration

@Suppress("unused")
class ObjectUnderTestInstantiationTest {
  @ObjectUnderTest
  private lateinit var ejbComponent: MyEjbComponent

  @ObjectUnderTest
  private lateinit var privateConstructorClass: PrivateConstructorClass

  @ObjectUnderTest
  private lateinit var noArgsConstructorClass: NoArgsConstructorClass

  private val configuration = DefaultNeedleConfiguration()

  @Test(expected = ObjectUnderTestInstantiationException::class)
  fun testInterfaceInstantiation() {
    setInstanceIfNotNull("ejbComponent")
  }

  @Test(expected = ObjectUnderTestInstantiationException::class)
  fun testNoArgConstructorInstantiation() {
    setInstanceIfNotNull("noArgsConstructorClass")
  }

  @Test(expected = ObjectUnderTestInstantiationException::class)
  fun testNoPublicConstructorInstantiation() {
    setInstanceIfNotNull("privateConstructorClass")
  }

  private fun setInstanceIfNotNull(fieldName: String) {
    val needleTestcase = NeedleInjector(InjectionConfiguration(configuration))
    val field = ObjectUnderTestInstantiationTest::class.java.getDeclaredField(fieldName)
    val objectUnderTestAnnotation: ObjectUnderTest = field.getAnnotation(ObjectUnderTest::class.java)

    configuration.reflectionHelper.invokeMethod(needleTestcase, "setInstanceIfNotNull", field, objectUnderTestAnnotation, this)
  }
}