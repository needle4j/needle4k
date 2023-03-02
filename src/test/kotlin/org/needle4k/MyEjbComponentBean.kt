package org.needle4k

import javax.inject.Inject
import java.util.*

class MyEjbComponentBean : MyEjbComponent {
  @Inject
  private lateinit var  testInjection: String

  val queue: Queue<*>? = null

  override fun doSomething(): String {
    return "Hello World"
  }
}