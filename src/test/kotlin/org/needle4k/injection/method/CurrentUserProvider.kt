package org.needle4k.injection.method

import org.needle4k.db.User
import org.needle4k.injection.CurrentUser
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionTargetInformation

object CurrentUserProvider : InjectionProvider<User> {
  val currentUser: User = User()

  override fun verify(information: InjectionTargetInformation<*>) = information.getAnnotation(CurrentUser::class.java) != null

  override fun <T> getInjectedObject(injectionTargetType: Class<T>) = currentUser as T

  override fun getKey(information: InjectionTargetInformation<*>) = CurrentUser::class.java
}