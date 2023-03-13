package org.needle4k.mock

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito

class MockitoProviderTest {
  private val mockitoProvider = MockitoProvider()

  @Test
  fun shouldCreateMockComponent() {
    val mapMock = mockitoProvider.createMockComponent(Map::class.java)!!
    val key = "key"
    val value = "value"

    Mockito.`when`(mapMock[key]).thenReturn(value)
    assertEquals(value, mapMock[key])
  }

  @Test
  fun shouldCreateSpyComponent() {
    var mapSpy: MutableMap<String?, String?> = HashMap()
    mapSpy = mockitoProvider.createSpyComponent(mapSpy)
    mapSpy["foo"] = "a"
    Mockito.`when`(mapSpy["bar"]).thenReturn("b")
    assertEquals(mapSpy["foo"], "a")
    assertEquals(mapSpy["bar"], "b")
    Mockito.verify<Map<String?, String?>>(mapSpy)["foo"]
    Mockito.verify<Map<String?, String?>>(mapSpy)["bar"]
    Mockito.verify(mapSpy)["foo"] = "a"
    Mockito.verifyNoMoreInteractions(mapSpy)
  }

  @Test
  fun shouldSkipCreateMockComponentForFinalType() {
    assertNull(mockitoProvider.createMockComponent(String::class.java))
  }

  @Test
  fun shouldSkipCreateMockComponentForPrimitiveType() {
    assertNull(mockitoProvider.createMockComponent(Int::class.java))
  }

  @Test
  fun shouldSkipCreateSpyComponentForPrimitive() {
    assertEquals("foo", mockitoProvider.createSpyComponent("foo"))
    assertEquals(1, mockitoProvider.createSpyComponent(1))
  }
}