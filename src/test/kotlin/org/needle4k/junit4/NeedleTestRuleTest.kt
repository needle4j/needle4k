package org.needle4k.junit4

import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.doThrow
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.testrule.NeedleTestRule
import javax.inject.Inject

@Suppress("CdiInjectionPointsInspection")
class NeedleTestRuleTest {
  class DummyTarget {
    @Inject
    private lateinit var runner: Runnable

    fun execute() {
      runner.run()
    }
  }

  @ObjectUnderTest
  private lateinit var dummyTarget: DummyTarget

  @Inject
  private lateinit var runnerMock: Runnable

  @Rule
  @JvmField
  val needle = NeedleTestRule(this)

  @Test(expected = UnsupportedOperationException::class)
  fun shouldCreateClassAndExecute() {
    assertNotNull(dummyTarget)
    assertNotNull(runnerMock)

    Mockito.`when`(runnerMock.run()).doThrow(UnsupportedOperationException())

    dummyTarget.execute()
  }
}