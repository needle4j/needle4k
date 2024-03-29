package org.needle4k.db

import org.needle4k.NeedleSession
import org.needle4k.db.operation.DBOperation
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.InjectionTargetInformation
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.EntityTransaction
import javax.sql.DataSource

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
class JPAInjectionProvider(val configuration: JPAInjectorConfiguration) : InjectionProvider<Any> {
  private val injectionProviderMap: Map<Class<*>, InjectionProvider<*>> = mapOf(
    DataSource::class.java to DataSourceProvider(),
    EntityManager::class.java to EntityManagerProvider(configuration.entityManager),
    EntityManagerFactory::class.java to EntityManagerFactoryProvider(configuration.entityManagerFactory),
    EntityTransaction::class.java to EntityTransactionProvider(configuration.entityManager),
    TransactionHelper::class.java to TransactionHelperProvider(configuration.transactionHelper)
  )

  /**
   * Execute setup database operation, if configured.
   *
   * @throws Exception thrown if an error occurs
   */
  fun before() {
    configuration.dbOperation.setUpOperation()
  }

  /**
   * Execute tear down database operation, if configured.
   *
   * @throws Exception thrown if an error occurs
   */
  fun after() {
    configuration.dbOperation.tearDownOperation()
    configuration.entityManager.clear()
  }

  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
    val injectionProvider = getInjectionProvider(injectionTargetInformation.injectedObjectType)

    return injectionProvider?.verify(injectionTargetInformation) ?: false
  }

  override fun getInjectedObject(injectionTargetType: Class<*>) =
    getInjectionProvider(injectionTargetType)?.getInjectedObject(injectionTargetType)
      ?: throw IllegalStateException("getInjectedObject: $injectionTargetType")

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>): Any =
    getInjectionProvider(injectionTargetInformation.injectedObjectType)?.getKey(injectionTargetInformation)
      ?: throw IllegalStateException("getKey: $injectionTargetInformation")

  private fun getInjectionProvider(type: Class<*>) = injectionProviderMap[type]

  override fun initialize(needleSession: NeedleSession) {
    injectionProviderMap.values.forEach { it.initialize(needleSession) }
  }
}