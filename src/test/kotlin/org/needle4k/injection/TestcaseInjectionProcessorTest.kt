package org.needle4k.injection

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.needle4k.MyComponentBean
import org.needle4k.MyEjbComponent
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule
import org.needle4k.mock.MockProvider
import java.util.*
import javax.annotation.Resource
import javax.ejb.EJB
import javax.ejb.SessionContext
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceContext

@Suppress("CdiInjectionPointsInspection")
class TestcaseInjectionProcessorTest {
  @Rule
  @JvmField
  var needleRule: NeedleRule = NeedleRule()

  @ObjectUnderTest
  private lateinit var bean: MyComponentBean

  @Inject
  private lateinit var provider: MockProvider

  @PersistenceContext
  private lateinit var entityManager: EntityManager

  @Inject
  private lateinit var entityManagerFactory: EntityManagerFactory

  @EJB
  private lateinit var myEjbComponent: MyEjbComponent

  @Resource
  private lateinit var sessionContext: SessionContext

  @Resource(mappedName = "queue1")
  private lateinit var queue1: Queue<*>

  @Resource(mappedName = "queue2")
  private lateinit var queue2: Queue<*>

  @Test
  fun testTestcaseInjection() {
    assertNotNull(queue1)
    assertSame(queue1, bean.queue1)
    assertNotNull(queue2)
    assertSame(queue2, bean.queue2)
    assertNotNull(sessionContext)
    assertSame(sessionContext, bean.sessionContext)
    assertNotNull(myEjbComponent)
    assertSame(myEjbComponent, bean.myEjbComponent)
    assertNotNull(entityManagerFactory)
    assertSame(entityManagerFactory, bean.entityManagerFactory)
    assertNotNull(entityManager)
    assertSame(entityManager, bean.entityManager)
    assertNotNull(provider)
  }
}