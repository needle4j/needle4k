package org.needle4k.junit4.testrule

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.needle4k.NeedleInjector
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.injection.InjectionProvider

/**
 *
 * JUnit [TestRule] for the initialization of the test. The Rule processes
 * and initializes all fields annotated with [ObjectUnderTest].
 *
 *
 *
 * This is an updated Rule to reflect the API change (MethodRule vs. TestRule)
 * in junit. Using this TestRule implementation has the drawback that the
 * calling test-instance has to be passed when the Rule is created, since the
 * new junit api does not pass the caller to the statement execution.
 *
 *
 * Using this Rule enables the [RuleChain]s feature of JUnit 4.
 *
 * <pre>
 * Example:
 *
 * public class UserDaoBeanTest {
 *
 * &#064;Rule
 * public final NeedleTestRule needle = new NeedleTestRule(this);
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
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm - Akquinet AG
 * @author Jan Galinski - Holisticon AG (jan.galinski@holisticon.de)
 * @see NeedleRule
 *
 * @see NeedleInjector
 */
class NeedleTestRule @JvmOverloads constructor(
  private val testInstance: Any,
  needleConfiguration: NeedleConfiguration = DefaultNeedleConfiguration.INSTANCE,
  vararg injectionProviders: InjectionProvider<*>
) : TestRule {
  private val needleInjector = NeedleInjector(InjectionConfiguration(needleConfiguration), *injectionProviders)

  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      @Throws(Throwable::class)
      override fun evaluate() {
        needleInjector.initTestInstance(testInstance)
        base.evaluate()
      }
    }
  }
}