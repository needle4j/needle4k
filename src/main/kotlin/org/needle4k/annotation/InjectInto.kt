package org.needle4k.annotation

/**
 * Declare that an object is to be injected into a [ObjectUnderTest] instance.
 *
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Jan Galinski, Holisticon AG (jan.galinski@holisticon.de)
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectInto(
  /**
   * Id of the object under test. This is the field name of the component, by
   * default. You can specify the id within the
   * `@[ObjectUnderTest]` annotation.
   *
   * @see ObjectUnderTest.id
   */
  val targetComponentId: String,

  /**
   * (Optional) fieldName of the injection target
   *
   *
   *
   * Default is the assignable type
   */
  val fieldName: String = ""
)