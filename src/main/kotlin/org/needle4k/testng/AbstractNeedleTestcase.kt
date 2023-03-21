package org.needle4k.testng

import org.needle4k.AbstractNeedleSession
import org.needle4k.NeedleInjector
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.injection.InjectionProvider
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod

@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractNeedleTestcase @JvmOverloads constructor(
  needleInjector: NeedleInjector = NeedleInjector(InjectionConfiguration(DefaultNeedleConfiguration())),
  vararg injectionProviders: InjectionProvider<*>
) : AbstractNeedleSession(needleInjector, *injectionProviders) {
  @BeforeMethod
  fun beforeNeedleTestcase() {
    runBeforeTest(this)
  }

  @AfterMethod
  fun afterNeedleTestcase() {
    runAfterTest()
  }
}