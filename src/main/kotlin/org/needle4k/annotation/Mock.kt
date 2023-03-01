package org.needle4k.annotation

/**
 * Allows shorthand mock creation with the configured [MockProvider]
 *
 * <pre>
 * Example:
 *
 * public void Test {
 *
 * &#064;Rule
 * public NeedleRule needle = new NeedleRule();
 *
 * &#064;Mock
 * private Queue queue;
 *
 * &#064;Test
 * public void test(){
 * ...
 * }
 * }
</pre> *
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Mock 