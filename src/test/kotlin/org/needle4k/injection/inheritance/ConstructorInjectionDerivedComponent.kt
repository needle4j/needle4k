package org.needle4k.injection.inheritance

import org.needle4k.MyComponent
import javax.inject.Inject

class ConstructorInjectionDerivedComponent @Inject constructor(val component: MyComponent) :
  ConstructorInjectionBaseComponent(component) {

  val myComponentFromBase: MyComponent
    get() = super.myComponent
}