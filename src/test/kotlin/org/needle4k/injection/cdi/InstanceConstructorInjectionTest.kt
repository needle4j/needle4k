package org.needle4k.injection.cdi

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule
import javax.enterprise.inject.Instance
import javax.inject.Inject

class InstanceConstructorInjectionTest {
  @Rule
  @JvmField
  val needleRule = NeedleRule()

  @ObjectUnderTest
  private lateinit var component: InstanceConstructorInjectionBean

  @Inject
  private lateinit var instance: Instance<InstanceTestBean>

  @Inject
  private lateinit var runnableInstances: Instance<Runnable>

  @Test
  fun testInstanceMethodInjection() {
    Assert.assertNotNull(instance)
    Assert.assertNotNull(runnableInstances)
    Assert.assertNotSame(instance, runnableInstances)
    Assert.assertNotNull(component.instance)
    Assert.assertSame(instance, component.instance)
  }
}

class InstanceConstructorInjectionBean @Inject constructor(val instance: Instance<InstanceTestBean>)