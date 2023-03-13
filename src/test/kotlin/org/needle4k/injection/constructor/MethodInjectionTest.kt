package org.needle4k.injection.constructor

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.injection.constructor.CurrentUserProvider.currentUser
import org.needle4k.junit4.NeedleRule

class MethodInjectionTest {
  @Rule
  @JvmField
  var needleRule = NeedleRule(CurrentUserProvider)

  @ObjectUnderTest
  private lateinit var userDao: MethodInjectionBean

  @Test
  fun testSetterInjection() {
    Assert.assertNotNull(userDao.user)
  }

  @Test
  fun testSetterInjection_Qualifier() {
    val currentUser2 = userDao.currentUser
    Assert.assertSame(currentUser, currentUser2)
  }

  @Test
  fun testMultipleMethodInjection() {
    Assert.assertNotNull(userDao.queue1)
    Assert.assertNotNull(userDao.queue2)
  }
}