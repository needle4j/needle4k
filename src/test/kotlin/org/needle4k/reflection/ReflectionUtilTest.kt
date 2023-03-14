package org.needle4k.reflection

import javax.annotation.Resource
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.needle4k.MyComponentBean
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.db.Address
import org.needle4k.injection.InjectionTargetInformation
import java.lang.reflect.Field
import java.lang.reflect.Method

class ReflectionUtilTest {
  private val configuration = DefaultNeedleConfiguration()
  private val objectUnderTest = configuration.reflectionHelper

  @Test
  fun testCanLookupPrivateFieldFromSuperclass() {
    val sample = DerivedClass()
    val result = objectUnderTest.getAllFieldsWithAnnotation(sample, MyAnnotation::class.java)

    assertThat(result).hasSize(1)
  }

  @Test
  fun testSetFieldValue() {
    val sample = DerivedClass()

    objectUnderTest.setFieldValue(sample, "aPrivateField", "aValue")
    assertEquals(sample.aPrivateField, "aValue")

    objectUnderTest.setFieldValue(sample, SampleClass::class.java, "aPrivateField", "otherValue")
    assertEquals(sample.aPrivateField, "otherValue")
  }

  @Test
  fun testGetAllFields() {
    assertThat(objectUnderTest.getAllFields(DerivedClass::class.java)).hasSize(5)
  }

  @Test
  fun testAllAnnotatedFields() {
    val allAnnotatedFields = objectUnderTest.getAllAnnotatedFields(MyComponentBean::class.java)
    assertEquals(4, allAnnotatedFields.size)

    val list = allAnnotatedFields[Resource::class.java]!!
    assertEquals(3, list.size)
  }

  @Test
  fun testInvokeMethod() {
    val invokeMethod = objectUnderTest.invokeMethod(this, "test") as String

    assertEquals("Hello World", invokeMethod)
  }

  @Test
  fun testGetFieldValue() {
    val address = Address()

    objectUnderTest.setField("id", address, 1L)
    assertEquals(1L, objectUnderTest.getFieldValue(address, Address::class.java, "id"))
  }

  @Test(expected = IllegalArgumentException::class)
  fun testGetFieldValue_Exception() {
    val address = Address()

    assertEquals(1L, objectUnderTest.getFieldValue(address, Address::class.java, "notexisting"))
  }

  @Test(expected = NoSuchElementException::class)
  fun testGetField_NoSuchField() {
    Assert.assertNull(objectUnderTest.getField(String::class.java, "notexisting"))
  }

  @Test
  fun testGetField_DerivedClass() {
    assertNotNull(objectUnderTest.getField(DerivedClass::class.java, "aPrivateField"))
  }

  @Test
  fun testGetMethodAndInvoke() {
    val method = objectUnderTest.getMethod(
      DerivedClass::class.java, "testGetMethod", String::class.java, Int::class.javaPrimitiveType!!, Any::class.java
    )
    assertNotNull(method)

    val result = objectUnderTest.invokeMethod(method, DerivedClass(), "Hello", 1, "")
    assertEquals("Hello", result.toString())
  }

  @Test
  fun testGetMethod() {
    assertThat(objectUnderTest.getMethods(DerivedClass::class.java)).hasSize(13)
  }

  @Test(expected = UnsupportedOperationException::class)
  fun testInvokeMethod_Exception() {
    objectUnderTest.invokeMethod(this, "testException")
  }

  @Test
  fun testGetAllFieldsAssignableFrom() {
    val allFieldsAssignableFromBoolean = objectUnderTest.getAllFieldsAssignableFrom(Boolean::class.java, DerivedClass::class.java)
    assertEquals(1, allFieldsAssignableFromBoolean.size)
    val allFieldsAssignableFromList = objectUnderTest.getAllFieldsAssignableFrom(List::class.java, DerivedClass::class.java)
    assertEquals(2, allFieldsAssignableFromList.size)
    val allFieldsAssignableFromCollection =
      objectUnderTest.getAllFieldsAssignableFrom(Collection::class.java, DerivedClass::class.java)
    assertEquals(2, allFieldsAssignableFromCollection.size)

    val g: List<Field> = objectUnderTest.getAllFieldsAssignableFrom(String::class.java, DerivedClass::class.java)
    assertEquals(2, g.size)
  }

  @Test
  fun testCreateInstance() {
    assertNotNull(objectUnderTest.createInstance(HashMap::class.java))
    assertEquals("Hello", objectUnderTest.createInstance(String::class.java, String::class.java to "Hello"))
  }

  @Test(expected = Exception::class)
  fun testCreateInstance_Exception() {
    objectUnderTest.createInstance(InjectionTargetInformation::class.java)
  }

  @Test
  fun testInvokeMethod_checkArgumentsWithPrimitives() {
    val derivedClass = DerivedClass()
    val intValue = 1
    val floatValue = 0f
    val charValue = 'c'
    val booleanValue = true
    val longValue = 10L
    val byteValue: Byte = 2
    val shortValue: Short = 32
    val doubleValue = 24.1

    val resultPrimitives = objectUnderTest.invokeMethod(
      derivedClass, "testInvokeWithPrimitive", intValue,
      floatValue, charValue, booleanValue, longValue, byteValue, shortValue, doubleValue
    )
    assertEquals(true, resultPrimitives)

    val resultObjects = objectUnderTest.invokeMethod(
      derivedClass, "testInvokeWithObjects", intValue,
      floatValue, charValue, booleanValue, longValue, byteValue, shortValue, doubleValue
    )
    assertEquals(true, resultObjects)
  }

  @Test
  fun testInvokeMethod_checkArgumentsWithObjects() {
    val derivedClass = DerivedClass()
    val intValue = 1
    val floatValue = 0f
    val charValue = 'c'
    val booleanValue = true
    val longValue = 10L
    val byteValue: Byte = 2
    val shortValue: Short = 32
    val doubleValue = 24.1

    val resultPrimitives = objectUnderTest.invokeMethod(
      derivedClass, "testInvokeWithPrimitive", intValue,
      floatValue, charValue, booleanValue, longValue, byteValue, shortValue, doubleValue
    )
    assertEquals(true, resultPrimitives)

    val resultObjects = objectUnderTest.invokeMethod(
      derivedClass, "testInvokeWithObjects", intValue,
      floatValue, charValue, booleanValue, longValue, byteValue, shortValue, doubleValue
    )
    assertEquals(true, resultObjects)
  }

  @Test(expected = IllegalArgumentException::class)
  fun testInvokeMethod_NoSuchMethod() {
    val derivedClass = DerivedClass()
    objectUnderTest.invokeMethod(derivedClass, "testInvokeWithPrimitive")
  }

  @Test(expected = IllegalArgumentException::class)
  fun testInvokeMethod_WithWrongParameter() {
    objectUnderTest.invokeMethod(this, "test", 1.0)
  }

  @Test
  fun shouldFindAllMethodsWithMyAnnotation() {
    val result: List<Method> = objectUnderTest.getAllMethodsWithAnnotation(DerivedClass::class.java, MyAnnotation::class.java)
    assertEquals(result.size, 2)
  }

  @Suppress("unused")
  private fun test() = "Hello World"

  @Suppress("unused", "UNUSED_PARAMETER")
  private fun test(value: Int) = ""

  @Suppress("unused")
  private fun testException() {
    throw UnsupportedOperationException()
  }
}