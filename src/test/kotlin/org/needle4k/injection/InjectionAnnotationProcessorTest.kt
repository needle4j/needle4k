package org.needle4k.injection

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.MyEjbComponentBean
import org.needle4k.annotation.InjectInto
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.db.User
import org.needle4k.junit4.NeedleRule
import java.util.*
import java.util.concurrent.LinkedBlockingDeque

class InjectionAnnotationProcessorTest {
  @Rule
  @JvmField
  val needleRule = NeedleRule()

  @ObjectUnderTest
  private val _userDao1 = UserDao()

  @ObjectUnderTest
  private val _userDao2 = UserDao()

  @ObjectUnderTest(id = "testInjectionId")
  private lateinit var bean: MyEjbComponentBean

  @InjectInto(targetComponentId = "testInjectionId")
  private val test = "Hello"

  @InjectIntoMany
  private val _user: User = User()

  @InjectIntoMany(
    value = [InjectInto(
      targetComponentId = "testInjectionId",
      fieldName = "queue"
    ), InjectInto(targetComponentId = "_userDao2")]
  )
  private val queue: Queue<*> = LinkedBlockingDeque<Any>()

  @Test
  fun testInjectMany() {
    Assert.assertSame(_user, _userDao1.currentUser)
    Assert.assertSame(_user, _userDao2.currentUser)
  }

  @Test
  fun testInjectManyWithInjectInto() {
    Assert.assertSame(queue, _userDao2.queue)
    Assert.assertSame(queue, bean.queue)

    // these are not the same since userDao1 gets a default mock injected
    Assert.assertNotSame(queue, _userDao1.queue)
  }

  @Test
  @Throws(Exception::class)
  fun testInjectIntoById() {
    Assert.assertSame(test, bean.testInjection)
  }
}