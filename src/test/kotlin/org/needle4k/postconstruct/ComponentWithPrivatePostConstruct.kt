package org.needle4k.postconstruct

import javax.annotation.PostConstruct
import javax.inject.Inject

class ComponentWithPrivatePostConstruct {
  @Inject
  private lateinit var component: DependentComponent

  @PostConstruct
  @Suppress("unused")
  private fun postConstruct() {
    component.count()
  }
}