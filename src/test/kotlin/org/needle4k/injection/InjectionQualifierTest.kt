package org.needle4k.injection

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.needle4k.NeedleInjector
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.db.User
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class InjectionQualifierTest {
  private val currentUser: User = User()
  private val currentUserProvider: InjectionProvider<User> = object : InjectionProvider<User> {
    override fun verify(information: InjectionTargetInformation<*>): Boolean {
      return information.getAnnotation(CurrentUser::class.java) != null
    }

    override fun <T> getInjectedObject(injectionTargetType: Class<T>): T = currentUser as T

    override fun getKey(information: InjectionTargetInformation<*>) = CurrentUser::class.java
  }

  @Inject
  @field:CurrentUser
  private lateinit var currentUserToInject: User

  @Inject
  private lateinit var user: User

  @Inject
  private lateinit var needleInjector: NeedleInjector

  @Rule
  @JvmField
  var needleRule: NeedleRule = NeedleRule(currentUserProvider)

  @ObjectUnderTest
  private lateinit var userDao: UserDao

  @Test
  fun testInject() {
    assertNotNull(userDao)
    assertNotNull(needleInjector)
    assertSame(currentUser, userDao.currentUser)
    assertNotNull(userDao.user)
    assertNotSame(currentUser, userDao.user)
    assertEquals(currentUser, needleRule.getInjectedObject(CurrentUser::class.java))
  }

  @Test
  fun testTestInjection() {
    assertNotNull(user)
    assertNotNull(currentUserToInject)
  }
}