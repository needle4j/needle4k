package org.needle4k.injection.constructor

import org.needle4k.db.User
import org.needle4k.injection.CurrentUser
import java.util.*
import javax.inject.Inject

@Suppress("unused")
class MethodInjectionBean {
  @set:Inject
  lateinit var user: User

  lateinit var currentUser: User

  lateinit var queue1: Queue<*>
    private set

  lateinit var queue2: Queue<*>
    private set

  @Inject
  fun initCurrentUser(@CurrentUser user: User) {
    currentUser = user
  }

  @Inject
  fun initQueues(queue1: Queue<*>, queue2: Queue<*>) {
    this.queue1 = queue1
    this.queue2 = queue2
  }
}