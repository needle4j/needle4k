package org.needle4k.junit4

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.needle4k.NeedleInjector
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.db.JPAInjector
import org.needle4k.db.JPAInjectorConfiguration
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.LazyInjectionProvider
import javax.persistence.EntityManager

/**
 * JUnit [MethodRule] for the initialization of the test. The Rule
 * processes and initializes all fields annotated with [org.needle4k.annotation.ObjectUnderTest].
 *
 * <pre>
 * Example:
 *
 * public class UserDaoBeanTest {
 *
 * &#064;Rule
 * public NeedleRule needle = new NeedleRule();
 *
 * &#064;ObjectUnderTest
 * private UserDaoBean userDao;
 *
 * &#064;Test
 * public void test() {
 * ...
 * userDao.someAction();
 * ...
 * }
 * }
 *
</pre> *
 *
 * @see NeedleInjector
 */
open class NeedleRule(val needleInjector: NeedleInjector, vararg injectionProviders: InjectionProvider<*>) : MethodRule {
  private val methodRuleChain = ArrayList<MethodRule>()

  val needleConfiguration get() = needleInjector.configuration.needleConfiguration
  val jpaInjector: JPAInjector? get() = needleInjector.configuration.getInjectionProvider(JPAInjector::class.java)
  val jpaInjectorConfiguration: JPAInjectorConfiguration
    get() = jpaInjector?.configuration
      ?: throw IllegalStateException("NeedleRule was not configured with JPA injection provider")
  val entityManager: EntityManager get() = jpaInjectorConfiguration.entityManager

  constructor(vararg injectionProviders: InjectionProvider<*>)
      : this(NeedleInjector(InjectionConfiguration(DefaultNeedleConfiguration.INSTANCE)), *injectionProviders)

  init {
    needleInjector.addInjectionProvider(*injectionProviders)
    needleInjector.addInjectionProvider(LazyInjectionProvider(NeedleInjector::class.java) { needleInjector })
    needleInjector.addInjectionProvider(LazyInjectionProvider(JPAInjector::class.java) {
      jpaInjector
        ?: throw IllegalStateException("NeedleRule was not configured with JPA injection provider")
    })
    needleInjector.addInjectionProvider(LazyInjectionProvider(JPAInjectorConfiguration::class.java) { jpaInjectorConfiguration })
  }

  fun withJPAInjection(): NeedleRule {
    if (!needleInjector.configuration.hasInjectionProvider(JPAInjector::class.java)) {
      needleInjector.addInjectionProvider(JPAInjector(JPAInjectorConfiguration(needleConfiguration)))
    }

    return this
  }

  /**
   * Add rule to method call chain
   *
   * @param rule - outer method rule
   * @return [NeedleRule]
   */
  @Suppress("unused")
  fun withOuter(rule: MethodRule): NeedleRule {
    if (rule is InjectionProvider<*>) {
      needleInjector.addInjectionProvider(rule as InjectionProvider<*>)
    }

    methodRuleChain.add(0, rule)

    return this
  }

  /**
   * {@inheritDoc} Before evaluation of the base statement, the test instance will be initialized.
   */
  override fun apply(base: Statement, method: FrameworkMethod, target: Any): Statement {
    var appliedStatement = base

    for (rule in methodRuleChain) {
      appliedStatement = rule.apply(appliedStatement, method, target)
    }

    return statement(appliedStatement, target)
  }

  fun <X> getInjectedObject(key: Any): X? = needleInjector.getInjectedObject<X>(key)

  fun <X> getInjectedObject(key: Class<X>): X? = needleInjector.getInjectedObject<X>(key)

  private fun statement(base: Statement, target: Any): Statement {
    return object : Statement() {
      @Throws(Throwable::class)
      override fun evaluate() {
        try {
          needleInjector.initTestInstance(target)
          needleInjector.before()
          jpaInjector?.before()
          base.evaluate()
        } finally {
          needleInjector.after()
          jpaInjector?.after()
        }
      }
    }
  }
}