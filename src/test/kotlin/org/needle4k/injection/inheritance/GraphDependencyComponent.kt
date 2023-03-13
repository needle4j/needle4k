package org.needle4k.injection.inheritance

import org.needle4k.MyComponent
import javax.inject.Inject

class GraphDependencyComponent {
  @Inject
  lateinit var component: MyComponent
}