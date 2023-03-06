package org.needle4k.mock

import org.easymock.EasyMockSupport

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
class EasyMockProvider : EasyMockSupport(), MockProvider {
  /**
   * {@inheritDoc} By default a mock with nice behavior. Skipping creation, if
   * the type is final or primitive. For details, see the EasyMock
   * documentation.
   *
   * @return the mock object or null, if the type is final or primitive.
   */
  override fun <T> doCreateMockComponent(type: Class<T>): T = createNiceMock(type)
}