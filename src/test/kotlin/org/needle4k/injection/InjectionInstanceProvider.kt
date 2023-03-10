package org.needle4k.injection

import org.needle4k.annotation.ObjectUnderTest
import javax.inject.Inject
import javax.inject.Provider

@Suppress("unused")
class InjectionInstanceProvider {
  class A {
    @Inject
    private lateinit var b: Provider<B>
  }

  interface B

  @ObjectUnderTest
  private lateinit var a: A

  fun test() {
  }
}