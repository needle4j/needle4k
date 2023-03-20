package org.needle4k.postconstruct

import org.easymock.EasyMock
import org.easymock.EasyMock.replay
import org.easymock.EasyMock.verify
import org.junit.Assert.assertEquals
import org.junit.Test
import org.needle4k.NeedleContext
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.POST_CONSTRUCT_EXECUTE_STRATEGY
import org.needle4k.configuration.PostConstructExecuteStrategy
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.reflection.ReflectionUtil
import java.lang.reflect.Method
import javax.annotation.PostConstruct

/**
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Jan Galinski, Holisticon AG (jan.galinski@holisticon.de)
 */
class PostConstructProcessorTest {
  private val runnableMock = EasyMock.createStrictMock<Runnable>(Runnable::class.java)
  private val secondRunnableMock = EasyMock.createStrictMock<Runnable>(Runnable::class.java)

  /**
   * a dummy class without init()
   */
  open inner class A

  /**
   * a dummy class with init()
   */
  open inner class B : A() {
    @PostConstruct
    protected fun init() {
      runnableMock.run()
    }
  }

  /**
   * used to test postconstruct hierarchy
   */
  open inner class C : B() {
    @PostConstruct
    fun initC() {
      secondRunnableMock.run()
    }
  }

  private val needleConfiguration = DefaultNeedleConfiguration()
  private val injectionConfiguration = InjectionConfiguration(needleConfiguration)
  private val postConstructProcessor = injectionConfiguration.postConstructProcessor

  // This Processor test does not use the NeedleRule!
  @ObjectUnderTest(postConstruct = true)
  private val isConfiguredForPostConstructionButDoesNotContainMethod: A = A()

  // This Processor test does not use the NeedleRule!
  @ObjectUnderTest(postConstruct = true)
  private val isConfiguredForPostConstruction: B = B()

  // This Processor test does not use the NeedleRule!
  @ObjectUnderTest
  private val isNotConfiguredForPostConstruction: B = B()

  // This Processor test does not use the NeedleRule!
  @ObjectUnderTest(postConstruct = true)
  private val instanceAndParentClassHavePostconstructMethods = C()

  @Test
  fun testWithoutPostConstructMethod() {
    val context = NeedleContext(this, needleConfiguration)
    val objectUnderTestAnnotation = getObjectUnderTestAnnotation("isConfiguredForPostConstructionButDoesNotContainMethod")
    context.addObjectUnderTest(
      objectUnderTestAnnotation.id,
      isConfiguredForPostConstructionButDoesNotContainMethod, objectUnderTestAnnotation
    )
    replay(runnableMock)
    postConstructProcessor.process(context)
    verify(runnableMock)
  }

  @Test
  fun testWithPostConstructMethod() {
    runnableMock.run()
    replay(runnableMock)
    val context = NeedleContext(this, needleConfiguration)
    val objectUnderTestAnnotation = getObjectUnderTestAnnotation("isConfiguredForPostConstruction")
    context.addObjectUnderTest(
      objectUnderTestAnnotation.id, isConfiguredForPostConstruction,
      objectUnderTestAnnotation
    )
    postConstructProcessor.process(context)
    verify(runnableMock)
  }

  @Test
  fun testWithPostConstructMethod_NotConfigured() {
    replay(runnableMock)
    val context = NeedleContext(this, needleConfiguration)
    val objectUnderTestAnnotation = getObjectUnderTestAnnotation("isNotConfiguredForPostConstruction")
    context.addObjectUnderTest(
      objectUnderTestAnnotation.id, isNotConfiguredForPostConstruction,
      objectUnderTestAnnotation
    )
    postConstructProcessor.process(context)
    verify(runnableMock)
  }

  @Test
  fun shouldCallPostConstructOnInstanceAndParent() {
    runnableMock.run()
    secondRunnableMock.run()
    replay(runnableMock, secondRunnableMock)
    val context = NeedleContext(this, needleConfiguration)
    val objectUnderTestAnnotation = getObjectUnderTestAnnotation("instanceAndParentClassHavePostconstructMethods")
    context.addObjectUnderTest(
      objectUnderTestAnnotation.id, instanceAndParentClassHavePostconstructMethods,
      objectUnderTestAnnotation
    )
    postConstructProcessor.process(context)
    verify(runnableMock, secondRunnableMock)
  }

  @Test
  fun shouldFindTwoPostConstructMethodsForC() {
    val methods: Set<Method> = postConstructProcessor.getPostConstructMethods(C::class.java)

    assertEquals(methods.size.toLong(), 2)
  }

  @Test
  fun shouldExecuteAlways() {
    runnableMock.run()
    val context = NeedleContext(this, needleConfiguration)
    val objectUnderTestAnnotation = getObjectUnderTestAnnotation("isNotConfiguredForPostConstruction")
    context.addObjectUnderTest(
      objectUnderTestAnnotation.id, isConfiguredForPostConstruction,
      objectUnderTestAnnotation
    )

    replay(runnableMock)
    needleConfiguration.configurationProperties[POST_CONSTRUCT_EXECUTE_STRATEGY] = PostConstructExecuteStrategy.ALWAYS.name
    postConstructProcessor.process(context)
    verify(runnableMock)
  }

  @Test
  fun shouldExecuteNever() {
    val context = NeedleContext(this, needleConfiguration)
    val objectUnderTestAnnotation = getObjectUnderTestAnnotation("isConfiguredForPostConstruction")
    context.addObjectUnderTest(
      objectUnderTestAnnotation.id, isConfiguredForPostConstruction,
      objectUnderTestAnnotation
    )

    needleConfiguration.configurationProperties[POST_CONSTRUCT_EXECUTE_STRATEGY] = PostConstructExecuteStrategy.NEVER.name

    replay(runnableMock)
    postConstructProcessor.process(context)
    verify(runnableMock)
  }

  private fun getObjectUnderTestAnnotation(fieldName: String) =
    ReflectionUtil.getField(javaClass, fieldName).getAnnotation(ObjectUnderTest::class.java)
}