package org.needle4k.injection.method

import org.needle4k.db.User
import org.needle4k.injection.CurrentUser
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionTargetInformation

object CurrentUserProvider : InjectionProvider<User> {
  val currentUser: User = User()

  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>) = injectionTargetInformation.getAnnotation(CurrentUser::class.java) != null

  override fun getInjectedObject(injectionTargetType: Class<*>) = currentUser

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) = CurrentUser::class.java
}