package org.needle4k.injection

import java.util.*

class CustomInjectionTestComponent {
  @CustomInjectionAnnotation1
  lateinit var queue1: Queue<String>

  @CustomInjectionAnnotation2
  lateinit var queue2: Queue<String>

  @CustomInjectionAnnotation1
  lateinit var map: Map<String, String>
}