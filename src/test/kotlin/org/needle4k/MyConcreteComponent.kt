package org.needle4k

open class MyConcreteComponent : MyComponent {
  override fun testMock(): String {
    return toString()
  }
}