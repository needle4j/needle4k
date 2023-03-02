package org.needle4k

import javax.annotation.Resource
import javax.ejb.EJB
import javax.ejb.SessionContext
import javax.inject.Inject
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceContext

open class MyComponentBean : MyComponent {
  @PersistenceContext
  lateinit var entityManager: EntityManager

  @Inject
  lateinit var entityManagerFactory: EntityManagerFactory

  @EJB
  lateinit var myEjbComponent: MyEjbComponent

  @Resource
  lateinit var sessionContext: SessionContext

  @Resource(mappedName = "queue1")
  lateinit var queue1: Queue<*>

  @Resource(mappedName = "queue2")
  lateinit var queue2: Queue<*>

  override fun testMock(): String {
    queue2.clear()
    return myEjbComponent.doSomething()
  }
}