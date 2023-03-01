package org.needle4k.annotation

/**
 *  Declare that an object is to be injected into multiple [ObjectUnderTest] instances.
 *
 * <pre>
 * Example 1:
 *
 * &#064;InjectIntoMany
 * private User user = new User();
 *
 * Example 2:
 *
 * &#064;InjectIntoMany(value = {
 * InjectInto(targetComponentId = "obejctUnderTest1"),
 * InjectInto(targetComponentId = "obejctUnderTest2", fieldName = "user")
 * })
 * private User user = new User();
</pre> *
 *
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Jan Galinski, Holisticon AG (jan.galinski@holisticon.de)
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectIntoMany(
  /**
   * (Optional) the injection targets
   *
   *
   * Default are all [ObjectUnderTest] annotated fields
   */
  vararg val value: InjectInto = []
)