package org.needle4k.mock

import org.easymock.EasyMock
import org.easymock.EasyMockSupport
import org.needle4k.configuration.NeedleConfiguration

/**
 * An EasyMock specific [MockProvider] implementation. For details, see
 * the EasyMock documentation.
 *
 * <pre>
 * Example:
 *
 * public class Test {
 *
 * private EasyMockProvider mockProvider = new EasyMockProvider();
 *
 * &#064;Test
 * public void test() {
 *
 * UserDao userDao = mockProvider.createMock(UserDao.class);
 *
 * mockProvider.replayAll();
 *
 * // ... use mocks
 *
 * mockProvider.verifyAll();
 * }
 * }
</pre> *
 */
@Suppress("UNUSED_PARAMETER", "unused")
class EasyMockProvider(needleConfiguration: NeedleConfiguration) : EasyMockSupport(), MockProvider {
  /**
   * {@inheritDoc} By default a mock with nice behavior. Skipping creation, if
   * the type is final or primitive. For details, see the EasyMock
   * documentation.
   *
   * @return the mock object or null, if the type is final or primitive.
   */
  override fun <T> doCreateMockComponent(type: Class<T>): T = createNiceMock(type)

  /**
   * Resets the given mock object and turns them to a mock with nice behavior.
   * For details, see the EasyMock documentation.
   *
   * @param mock the mock object
   * @return the mock object
   */
  fun <X> resetToNice(mock: X): X {
    EasyMock.resetToNice(mock)
    return mock
  }

  /**
   * Resets the given mock objects and turns them to a mock with strict
   * behavior. For details, see the EasyMock documentation.
   *
   * @param mocks the mock objects
   */
  fun resetToStrict(vararg mocks: Any?) {
    EasyMock.resetToStrict(*mocks)
  }

  /**
   * Resets the given mock object and turns them to a mock with strict
   * behavior. For details, see the EasyMock documentation.
   *
   * @param mock the mock objects
   * @return the mock object
   */
  fun <X> resetToStrict(mock: X): X {
    EasyMock.resetToStrict(mock)
    return mock
  }

  /**
   * Resets the given mock objects and turns them to a mock with default
   * behavior. For details, see the EasyMock documentation.
   *
   * @param mocks the mock objects
   */
  fun resetToDefault(vararg mocks: Any?) {
    EasyMock.resetToDefault(*mocks)
  }

  /**
   * Resets the given mock object and turns them to a mock with default
   * behavior. For details, see the EasyMock documentation.
   *
   * @param mock the mock object
   * @return the mock object
   */
  fun <X> resetToDefault(mock: X): X {
    EasyMock.resetToDefault(mock)
    return mock
  }

  override fun reset() {}
}