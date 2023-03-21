package org.needle4k.mock

import org.easymock.EasyMock
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.MyComponent
import org.needle4k.MyComponentBean
import org.needle4k.MyEjbComponent
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.configuration.MOCK_PROVIDER_KEY
import org.needle4k.db.User
import org.needle4k.injection.UserDao
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject

@Suppress("CdiInjectionPointsInspection")
class EasyMockProviderTest {
  @Rule
  @JvmField
  val needleRule = NeedleRule().apply {
    needleConfiguration.configurationProperties[MOCK_PROVIDER_KEY] = EasyMockProvider::class.qualifiedName!!
  }

  @ObjectUnderTest(implementation = MyComponentBean::class)
  private lateinit var component: MyComponent

  @Inject
  private lateinit var mockProvider: MockProvider

  private val easymockProvider: EasyMockProvider get() = mockProvider as EasyMockProvider

  @Test
  fun testNiceMock() {
    val testMock: String = component.testMock()
    Assert.assertNull(testMock)
  }

  @Test
  fun testStrictMock() {
    val myEjbComponentMock: MyEjbComponent? = needleRule.getInjectedObject(MyEjbComponent::class.java)
    Assert.assertNotNull(myEjbComponentMock)
    EasyMock.resetToStrict(myEjbComponentMock)
    EasyMock.expect(myEjbComponentMock?.doSomething()).andReturn("Hello World")
    easymockProvider.replayAll()
    val testMock: String = component.testMock()
    Assert.assertEquals("Hello World", testMock)
    easymockProvider.verifyAll()
  }

  @Test(expected = IllegalStateException::class)
  fun testResetToStrict_Mocks() {
    val userMock: User = easymockProvider.createMock(User::class.java)
    val userDaoMock: UserDao = easymockProvider.createMock(UserDao::class.java)
    easymockProvider.resetToStrict(userMock, userDaoMock)
    userMock.id
    easymockProvider.replayAll()
    easymockProvider.verifyAll()
  }

  @Test(expected = IllegalStateException::class)
  fun testResetToStrict_Mock() {
    val userMock: User = easymockProvider.createMock(User::class.java)
    easymockProvider.resetToStrict(userMock)
    userMock.id
    easymockProvider.replayAll()
    easymockProvider.verifyAll()
  }
}