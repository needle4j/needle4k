package org.needle4k.injection.inheritance

import org.needle4k.MyComponent
import javax.inject.Inject

open class BaseComponent {
  @Inject
  private lateinit var component: MyComponent

  @Inject
  private lateinit var qualifier: MyComponent

  @Inject
  private lateinit var dependencyComponent: GraphDependencyComponent

  private lateinit var componentSetterInjection: MyComponent

  open val componentByFieldInjection: MyComponent
    get() = component

  @Inject
  fun setComponentBySetterBase(component: MyComponent) {
    componentSetterInjection = component
  }

  open val componentBySetter: MyComponent
    get() = componentSetterInjection
}