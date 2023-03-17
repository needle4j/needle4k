package org.needle4k

import java.util.*
import javax.inject.Inject

class MyEjbComponentBean : MyEjbComponent {
  @Inject
  lateinit var  testInjection: String

  val queue: Queue<*>? = null

  override fun doSomething(): String {
    return "Hello World"
  }
}