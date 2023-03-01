package org.needle4k.annotation

import kotlin.reflect.KClass


/**
 * Used to specify an object under test. The annotated fields will be initialized by the needle4k framework.
 *
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Jan Galinski, Holisticon AG (jan.galinski@holisticon.de)
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ObjectUnderTest(
  /**
   * (Optional) The implementation class of the object under test.
   *
   *
   * Default is the field type.
   */
  val implementation: KClass<*> = Void::class,

  /**
   * (Optional) The id of the object under test.
   *
   *
   * Default is the field name.
   */
  val id: String = "",

  /**
   * (Optional) execute @PostConstruct method of the class under test
   *
   * Note: Behavior in an inheritance hierarchy is not defined by the common
   * annotations specification
   *
   * Default is false
   */
  val postConstruct: Boolean = false
)