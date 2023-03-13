package org.needle4k.injection.inheritance

import org.needle4k.MyComponent
import javax.inject.Inject

class DerivedComponent : BaseComponent() {
  @Inject
  private lateinit var componentFieldInjectionDerived: MyComponent

  private lateinit var componentSetterInjectionDerived: MyComponent

  override val componentByFieldInjection: MyComponent
    get() = componentFieldInjectionDerived

  val componentFromBaseByFieldInjection: MyComponent
    get() = super.componentByFieldInjection

  @set:Inject
  override var componentBySetter: MyComponent
    get() = componentSetterInjectionDerived

    set(component) {
      componentSetterInjectionDerived = component
    }

  val componentFromBaseBySetter: MyComponent
    get() = super.componentBySetter
}