package org.needle4k.injection.constructor

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.db.User
import org.needle4k.injection.CurrentUser
import org.needle4k.injection.constructor.CurrentUserProvider.currentUser
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject

class ConstructorInjectionTest {
  @Rule
  @JvmField
  var needleRule = NeedleRule(CurrentUserProvider)

  @ObjectUnderTest
  private lateinit var constructorBean: ConstructorBean

  @Test
  fun testConstructorInjection() {
    Assert.assertNotNull(constructorBean.user)
    Assert.assertSame(currentUser, constructorBean.currentUser)
  }
}

class ConstructorBean @Inject constructor(val user: User, @param:CurrentUser val currentUser: User)