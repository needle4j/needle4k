package org.needle4k.injection

import org.needle4k.db.User
import java.util.*
import javax.inject.Inject

class UserDao {
  @Inject
  @field:CurrentUser
  lateinit var currentUser: User

  @Inject
  lateinit var user: User

  @Inject
  lateinit var queue: Queue<*>
}