package org.needle4k.injection.inheritance

import org.needle4k.MyComponent
import javax.inject.Inject

open class ConstructorInjectionBaseComponent @Inject constructor(val myComponent: MyComponent)