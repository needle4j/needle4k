package org.needle4k.postconstruct

open class DependentComponent {
  var counter = 0
    private set

  fun count() {
    counter++
  }
}