package org.needle4k.junit4

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.needle4k.AbstractNeedleRule
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
open class NeedleRule(needleInjector: NeedleInjector, vararg injectionProviders: InjectionProvider<*>) :
  AbstractNeedleRule(needleInjector, *injectionProviders), MethodRule {
  private val methodRuleChain = ArrayList<MethodRule>()

  constructor(vararg injectionProviders: InjectionProvider<*>)
      : this(NeedleInjector(InjectionConfiguration(DefaultNeedleConfiguration())), *injectionProviders)

  fun withJPAInjection(): NeedleRule {
    addJPAInjectionProvider()
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

  private fun statement(base: Statement, target: Any): Statement {
    return object : Statement() {
      override fun evaluate() {
        try {
          runBeforeTest(target)
          base.evaluate()
        } finally {
          runAfterTest()
        }
      }
    }
  }
}