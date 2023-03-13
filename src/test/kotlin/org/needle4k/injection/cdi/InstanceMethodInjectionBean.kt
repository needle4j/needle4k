package org.needle4k.injection.cdi

import javax.enterprise.inject.Instance
import javax.inject.Inject

class InstanceMethodInjectionBean {
  @set:Inject
  lateinit var instance: Instance<InstanceTestBean>
}