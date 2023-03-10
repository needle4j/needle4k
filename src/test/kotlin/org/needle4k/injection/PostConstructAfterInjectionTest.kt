package org.needle4k.injection

import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.configuration.MOCK_PROVIDER_KEY
import org.needle4k.junit4.NeedleRule
import org.needle4k.mock.EasyMockProvider
import javax.annotation.PostConstruct
import javax.inject.Inject

class PostConstructAfterInjectionTest {
  @Rule
  @JvmField
  var needleRule: NeedleRule = NeedleRule().apply {
    needleConfiguration.configurationProperties[MOCK_PROVIDER_KEY] = EasyMockProvider::class.qualifiedName!!
  }

  @ObjectUnderTest(postConstruct = true)
  private lateinit var objectUnderTest: PostConstructTestObjectUnderTest

  @ObjectUnderTest(postConstruct = true)
  @InjectIntoMany
  private lateinit var dummyA: PostConstructDummyA

  @Inject
  private lateinit var dummyB: PostConstructDummyB

  @Before
  fun initMocks() {
    dummyB = needleRule.getInjectedObject(PostConstructDummyB::class.java)!!
    Assert.assertEquals("precondition failed", dummyA, objectUnderTest.dummyA)
    Assert.assertEquals("precondition failed", dummyB, objectUnderTest.dummyB)
  }

  @Test
  fun testPostConstruct() {
    Assert.assertTrue(dummyA.isPostConstructCalledForDummy)
    Assert.assertTrue(objectUnderTest.isPostConstructCalledForDummy)
    Assert.assertTrue(objectUnderTest.isPostConstructCalledForTestObjectUnderTest)
  }
}

open class PostConstructDummyA {
  var isPostConstructCalledForDummy = false
    private set

  @PostConstruct
  fun initDummy() {
    isPostConstructCalledForDummy = true
  }
}

open class PostConstructDummyB {
  var isPostConstructCalledForDummy = false
    private set

  @PostConstruct
  fun initDummy() {
    isPostConstructCalledForDummy = true
  }
}

open class PostConstructTestObjectUnderTest : PostConstructDummyA() {
  var isPostConstructCalledForTestObjectUnderTest = false
    private set

  @Inject
  lateinit var dummyA: PostConstructDummyA

  @Inject
  lateinit var dummyB: PostConstructDummyB

  @Inject
  fun injectDummy(dummy: PostConstructDummyA) {
    dummyA = dummy
  }

  @PostConstruct
  fun initTestObjectUnderTest() {
    isPostConstructCalledForTestObjectUnderTest = true
  }
}
