package org.needle4k.test

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.reflection.ReflectionUtil
import org.needle4k.reflection.getAllFieldsWithSupportedAnnotation
import org.needle4k.registries.AnnotationRegistry
import javax.ejb.EJB
import javax.inject.Inject

@Suppress("UsePropertyAccessSyntax")
class AnnotationRegistryTest {
  private val configuration = DefaultNeedleConfiguration()
  private val objectUnderTest = AnnotationRegistry()
  private val ejb = ReflectionUtil.getAllFieldsWithAnnotation(TestClass::class.java, EJB::class.java).first()
    .getAnnotation(EJB::class.java)
  private val inject =
    ReflectionUtil.getAllFieldsWithAnnotation(TestClass::class.java, Inject::class.java).first()
      .getAnnotation(Inject::class.java)

  @BeforeEach
  fun patchConfiguration() {
    ReflectionUtil.setFieldValue(configuration, "injectionAnnotationRegistry", objectUnderTest)
  }

  @Test
  fun `Test AnnotationRegistry with classes`() {
    objectUnderTest.addAnnotation(EJB::class.java)
    objectUnderTest.addAnnotation(Inject::class.java)

    checkAnnotations()
  }

  @Test
  fun `Test AnnotationRegistry with class names`() {
    objectUnderTest.addAnnotation(EJB::class.java)
    objectUnderTest.addAnnotation(Inject::class.java.name)

    checkAnnotations()
  }

  @Test
  fun `Check annotation classes`() {
    assertThrows(IllegalArgumentException::class.java) { objectUnderTest.addAnnotation(HashMap::class.java.name) }

    val prefix = if(ReflectionUtil.forName("jakarta.inject.Inject") == null) "jakarta" else "javax"
    objectUnderTest.addAnnotation("$prefix.inject.Inject")
    assertThat(objectUnderTest.allAnnotations()).`as`("class not found").isEmpty()

    assertThat(TestClass::class.java.getAllFieldsWithSupportedAnnotation(configuration)).isEmpty()

    objectUnderTest.addAnnotation(EJB::class.java)
    assertThat(TestClass::class.java.getAllFieldsWithSupportedAnnotation(configuration)).hasSize(1)
  }

  private fun checkAnnotations() {
    assertThat(objectUnderTest.allAnnotations()).containsExactlyInAnyOrder(EJB::class.java, Inject::class.java)
    assertThat(objectUnderTest.isRegistered(ejb, inject)).isTrue()
    assertThat(objectUnderTest.isRegistered(EJB::class.qualifiedName!!,  Inject::class.qualifiedName!!)).isTrue()

    objectUnderTest.removeAnnotation(Inject::class.java)
    assertThat(objectUnderTest.allAnnotations()).containsExactlyInAnyOrder(EJB::class.java)
    assertThat(objectUnderTest.isRegistered(ejb, inject)).isTrue()
    assertThat(objectUnderTest.isRegistered(inject)).isFalse()
    assertThat(objectUnderTest.isRegistered(ejb)).isTrue()
  }
}

@Suppress("unused")
private class TestClass {
  @EJB
  private lateinit var jens: AnnotationRegistry

  @Inject
  private lateinit var hippe: AnnotationRegistry
}