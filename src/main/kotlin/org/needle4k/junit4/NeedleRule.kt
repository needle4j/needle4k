package org.needle4k.junit4

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.needle4k.NeedleInjector
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.injection.InjectionProvider

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
class NeedleRule constructor(
  val needleInjector: NeedleInjector,
  vararg injectionProviders: InjectionProvider<*>
) : MethodRule {
  private val methodRuleChain = ArrayList<MethodRule>()

  constructor(vararg injectionProviders: InjectionProvider<*>)
      : this(NeedleInjector(InjectionConfiguration(DefaultNeedleConfiguration.INSTANCE)), *injectionProviders)

  init {
    needleInjector.addInjectionProvider(*injectionProviders)
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

  fun <X> getInjectedObject(key:Class<X>): X? = needleInjector.getInjectedObject<X>(key)

  private fun statement(base: Statement, target: Any): Statement {
    return object : Statement() {
      @Throws(Throwable::class)
      override fun evaluate() {
        needleInjector.initTestInstance(target)
        base.evaluate()
      }
    }
  }

  /**
   * Encloses the added rule.
   *
   * @param rule - outer method rule
   * @return [NeedleRule]
   */
  fun withOuter(rule: MethodRule): NeedleRule {
    if (rule is InjectionProvider<*>) {
      needleInjector.addInjectionProvider(rule as InjectionProvider<*>)
    }

    methodRuleChain.add(0, rule)

    return this
  }
}