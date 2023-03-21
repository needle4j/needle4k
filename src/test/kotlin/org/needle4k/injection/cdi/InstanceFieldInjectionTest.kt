package org.needle4k.injection.cdi

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule
import javax.enterprise.inject.Instance
import javax.inject.Inject

@Suppress("CdiInjectionPointsInspection", "AssertBetweenInconvertibleTypes")
class InstanceFieldInjectionTest {
  @Rule
  @JvmField
  val needleRule = NeedleRule()

  @ObjectUnderTest
  private lateinit var component: InstanceFieldInjectionBean

  @Inject
  private lateinit var instance: Instance<InstanceTestBean>

  @Inject
  private lateinit var runnableInstances: Instance<Runnable>

  @Test
  fun testInstanceFieldInjection() {
    assertNotNull(instance)
    assertNotNull(runnableInstances)
    assertNotSame(instance, runnableInstances)
    assertSame(instance, component.instance)
  }
}

class InstanceFieldInjectionBean {
  @Inject
  lateinit var instance: Instance<InstanceTestBean>
}