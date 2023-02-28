package org.needle4k.reflection

import jakarta.annotation.Resource
import org.junit.Assert
import org.junit.Test
import org.needle4k.MyComponentBean
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.Address
import org.needle4k.injection.InjectionTargetInformation
import java.lang.reflect.Field
import java.lang.reflect.Method

class ReflectionUtilTest {
  private val configuration = NeedleConfiguration()
  private val objectUnderTest = configuration.reflectionHelper

  @Test
  fun testCanLookupPrivateFieldFromSuperclass() {
    val sample = DerivedClass()
    val result: List<Field> = objectUnderTest.getAllFieldsWithAnnotation(sample, MyAnnotation::class.java)
    Assert.assertEquals(result.size.toLong(), 1)
  }

  @Test
  fun testSetFieldValue() {
    val sample = DerivedClass()
    objectUnderTest.setFieldValue(sample, "aPrivateField", "aValue")
    Assert.assertEquals(sample.aPrivateField, "aValue")
    objectUnderTest.setFieldValue(sample, SampleClass::class.java, "aPrivateField", "otherValue")
    Assert.assertEquals(sample.aPrivateField, "otherValue")
  }

  @Test
  fun testGetAllFields() {
    val allFields: List<Field> = objectUnderTest.getAllFields(DerivedClass::class.java)
    Assert.assertEquals(allFields.size, 5)
  }

  @Test
  fun testAllAnnotatedFields() {
    val allAnnotatedFields: Map<Class<out Annotation?>, List<Field>> = objectUnderTest
      .getAllAnnotatedFields(MyComponentBean::class.java)
    Assert.assertEquals(4, allAnnotatedFields.size)
    val list = allAnnotatedFields[Resource::class.java]!!
    Assert.assertEquals(3, list.size)
  }

  @Test
  @Throws(Exception::class)
  fun testInvokeMethod() {
    val invokeMethod = objectUnderTest.invokeMethod(this, "test") as String
    Assert.assertEquals("Hello World", invokeMethod)
  }

  @Test
  fun testGetFieldValue() {
    val address = Address()
    objectUnderTest.setField("id", address, 1L)
    Assert.assertEquals(1L, objectUnderTest.getFieldValue(address, Address::class.java, "id"))
  }

  @Test(expected = IllegalArgumentException::class)
  fun testGetFieldValue_Exception() {
    val address = Address()
    Assert.assertEquals(1L, objectUnderTest.getFieldValue(address, Address::class.java, "notexisting"))
  }

  @Test(expected = NoSuchElementException::class)
  fun testGetField_NoSuchField() {
    Assert.assertNull(objectUnderTest.getField(String::class.java, "fieldName"))
  }

  @Test
  fun testGetField_DerivedClass() {
    Assert.assertNotNull(objectUnderTest.getField(DerivedClass::class.java, "aPrivateField"))
  }

  @Test
  @Throws(Exception::class)
  fun testGetMethodAndInvoke() {
    val method: Method = objectUnderTest.getMethod(
      DerivedClass::class.java, "testGetMethod", String::class.java, Int::class.javaPrimitiveType!!, Any::class.java
    )
    Assert.assertNotNull(method)
    val result: Any = objectUnderTest.invokeMethod(method, DerivedClass(), "Hello", 1, "")
    Assert.assertEquals("Hello", result.toString())
  }

  @Test
  fun testGetMethod() {
    val methods: List<Method> = objectUnderTest.getMethods(
      DerivedClass::class.java
    )
    Assert.assertEquals(13, methods.size)
  }

  @Test(expected = UnsupportedOperationException::class)
  @Throws(Exception::class)
  fun testInvokeMethod_Exception() {
    objectUnderTest.invokeMethod(this, "testException")
  }

  @Test
  fun testGetAllFieldsAssignableFrom() {
    val allFieldsAssignableFromBoolean = objectUnderTest.getAllFieldsAssignableFrom(
      Boolean::class.java, DerivedClass::class.java
    )
    Assert.assertEquals(1, allFieldsAssignableFromBoolean.size)
    val allFieldsAssignableFromList: List<Field> = objectUnderTest.getAllFieldsAssignableFrom(
      MutableList::class.java, DerivedClass::class.java
    )
    Assert.assertEquals(2, allFieldsAssignableFromList.size)
    val allFieldsAssignableFromCollection: List<Field> = objectUnderTest.getAllFieldsAssignableFrom(
      MutableCollection::class.java, DerivedClass::class.java
    )
    Assert.assertEquals(2, allFieldsAssignableFromCollection.size)
    val g: List<Field> = objectUnderTest.getAllFieldsAssignableFrom(
      String::class.java, DerivedClass::class.java
    )
    Assert.assertEquals(2, g.size)
  }

  @Test
  @Throws(Exception::class)
  fun testCreateInstance() {
    Assert.assertNotNull(objectUnderTest.createInstance(HashMap::class.java))
    Assert.assertEquals("Hello", objectUnderTest.createInstance(String::class.java, "Hello"))
  }

  @Test(expected = Exception::class)
  @Throws(Exception::class)
  fun testCreateInstance_Exception() {
    objectUnderTest.createInstance(InjectionTargetInformation::class.java)
  }

  @Test
  @Throws(Exception::class)
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
    val resultPrimatives: Any = objectUnderTest.invokeMethod(
      derivedClass, "testInvokeWithPrimitive", intValue,
      floatValue, charValue, booleanValue, longValue, byteValue, shortValue, doubleValue
    )
    Assert.assertEquals(true, resultPrimatives)
    val resultObjects: Any = objectUnderTest.invokeMethod(
      derivedClass, "testInvokeWithObjects", intValue,
      floatValue, charValue, booleanValue, longValue, byteValue, shortValue, doubleValue
    )
    Assert.assertEquals(true, resultObjects)
  }

  @Test
  @Throws(Exception::class)
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
    val resultPrimitives: Any = objectUnderTest.invokeMethod(
      derivedClass, "testInvokeWithPrimitive", intValue,
      floatValue, charValue, booleanValue, longValue, byteValue, shortValue, doubleValue
    )
    Assert.assertEquals(true, resultPrimitives)
    val resultObjects: Any = objectUnderTest.invokeMethod(
      derivedClass, "testInvokeWithObjects", intValue,
      floatValue, charValue, booleanValue, longValue, byteValue, shortValue, doubleValue
    )
    Assert.assertEquals(true, resultObjects)
  }

  @Test(expected = IllegalArgumentException::class)
  @Throws(Exception::class)
  fun testInvokeMethod_NoSuchMethod() {
    val derivedClass = DerivedClass()
    objectUnderTest.invokeMethod(derivedClass, "testInvokeWithPrimitive")
  }

  @Test(expected = IllegalArgumentException::class)
  @Throws(Exception::class)
  fun testInvokeMethod_WithWrongParameter() {
    objectUnderTest.invokeMethod(this, "test", 1.0)
  }

  @Test
  fun shouldFindAllMethodsWithMyAnnotation() {
    val result: List<Method> = objectUnderTest.getAllMethodsWithAnnotation(
      DerivedClass::class.java, MyAnnotation::class.java
    )
    Assert.assertEquals(result.size, 2)
  }

  @Suppress("unused")
  private fun test(): String {
    return "Hello World"
  }

  @Suppress("unused")
  private fun test(value: Int): String {
    return ""
  }

  @Suppress("unused")
  @Throws(UnsupportedOperationException::class)
  private fun testException() {
    throw UnsupportedOperationException()
  }
}