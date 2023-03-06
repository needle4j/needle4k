package org.needle4k.db

import org.needle4k.db.operation.DBOperation
import org.needle4k.db.jpa.TransactionHelper
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionTargetInformation
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.EntityTransaction

/**
 * Base class for a database test case. Executes optional database operation on
 * test setup and tear down.
 *
 * May be used as an injection provider for [EntityManager],
 * [EntityManagerFactory] and [EntityTransaction].
 *
 * @see InjectionProvider
 *
 * @see DBOperation
 */
open class DatabaseInjector(val configuration: DatabaseInjectorConfiguration) : InjectionProvider<Any> {
  private val injectionProviderMap: Map<Class<*>, InjectionProvider<*>> = mapOf(
    EntityManager::class.java to EntityManagerProvider(configuration.entityManager),
    EntityManagerFactory::class.java to EntityManagerFactoryProvider(configuration.entityManagerFactory),
    EntityTransaction::class.java to EntityTransactionProvider(configuration.entityManager),
    TransactionHelper::class.java to TransactionHelperProvider(configuration.transactionHelper)
  )

  /**
   * Execute tear down database operation, if configured.
   *
   * @throws Exception thrown if an error occurs
   */
  @Throws(Exception::class)
  fun after() {
    configuration.dbOperation.tearDownOperation()
    configuration.entityManager.clear()
  }

  /**
   * Execute setup database operation, if configured.
   *
   * @throws Exception thrown if an error occurs
   */
  @Throws(Exception::class)
  fun before() {
    configuration.dbOperation.setUpOperation()
  }

  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
    val injectionProvider = getInjectionProvider(injectionTargetInformation.injectedObjectType)

    return injectionProvider?.verify(injectionTargetInformation) ?:false
  }

  override fun <T> getInjectedObject(injectionTargetType: Class<T>): T =
    getInjectionProvider(injectionTargetType)?.getInjectedObject(injectionTargetType)
      ?: throw IllegalStateException("getInjectedObject: $injectionTargetType")

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any =
    getInjectionProvider(injectionTargetInformation.injectedObjectType)?.getKey(injectionTargetInformation)
      ?: throw IllegalStateException("getKey: $injectionTargetInformation")

  private fun getInjectionProvider(type: Class<*>) = injectionProviderMap[type]
}