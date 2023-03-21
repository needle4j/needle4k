package org.needle4k.postconstruct

import javax.annotation.PostConstruct

open class DependentComponent {
  var counter = 0
    private set

  fun count() {
    counter++
  }

  @PostConstruct
  @Suppress("unused")
  fun postConstruct() {
    count()
  }
}