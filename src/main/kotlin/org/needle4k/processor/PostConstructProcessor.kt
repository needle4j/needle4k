package org.needle4k.processor

import org.needle4k.NeedleContext
import org.needle4k.ObjectUnderTestInstantiationException
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.configuration.PostConstructExecuteStrategy
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.reflection.ReflectionUtil
import java.lang.reflect.Method

/**
 * Handles execution of postConstruction methods of instances marked with
 * [ObjectUnderTest.postConstruct]
 *
 *
 * Note: Behavior in an inheritance hierarchy is not defined by the common
 * annotations specification
 *
 *
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Jan Galinski, Holisticon AG (jan.galinski@holisticon.de)
 * @author Heinz Wilming, akquinet AG (heinz.wilming@akquinet.de)
 */
class PostConstructProcessor(private val configuration: InjectionConfiguration) : NeedleProcessor {
  private val postConstructAnnotations get() = configuration.needleConfiguration.postConstructAnnotationRegistry.allAnnotations()
  private val postConstructExecuteStrategy get() = configuration.needleConfiguration.postConstructExecuteStrategy

  /**
   * calls process(instance) for each object under test, only if field is
   * marked with [ObjectUnderTest](postConstruct=true), else ignored.
   *
   * @param context the NeedleContext
   */
  override fun process(context: NeedleContext) {
    if (postConstructExecuteStrategy !== PostConstructExecuteStrategy.NEVER) {
      val objectsUnderTestIds = context.objectsUnderTestIds

      for (objectUnderTestId in objectsUnderTestIds) {
        val objectUnderTestAnnotation = context.getObjectUnderTestAnnotation(objectUnderTestId)

        if (postConstructExecuteStrategy === PostConstructExecuteStrategy.ALWAYS ||
          objectUnderTestAnnotation != null && objectUnderTestAnnotation.postConstruct
        ) {
          val instance = context.getObjectUnderTest(objectUnderTestId)
            ?: throw ObjectUnderTestInstantiationException("Could not resolve @ObjectUnderTest with id $objectUnderTestId")
          process(instance)
        }
      }
    }
  }

  /**
   * invokes @PostConstruct annotated method
   *
   * @param instance
   * @throws ObjectUnderTestInstantiationException
   */
  private fun process(instance: Any) {
    val postConstructMethods = getPostConstructMethods(instance.javaClass)

    for (method in postConstructMethods) {
      try {
        ReflectionUtil.invokeMethod(method, instance)
      } catch (e: Exception) {
        throw ObjectUnderTestInstantiationException(
          "Error executing postConstruction method '${method.name}'", e
        )
      }
    }
  }

  /**
   * @return all instance methods that are marked as postConstruction methods
   */
  fun getPostConstructMethods(type: Class<*>): Set<Method> {
    val postConstructMethods: MutableSet<Method> = LinkedHashSet()

    for (postConstructAnnotation in postConstructAnnotations) {
      postConstructMethods.addAll(
        ReflectionUtil.getAllMethodsWithAnnotation(
          type,
          postConstructAnnotation
        )
      )
    }
    return postConstructMethods
  }
}