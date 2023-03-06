package org.needle4k.configuration

import org.slf4j.LoggerFactory
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

const val MOCK_PROVIDER_KEY = "mock.provider"
const val DB_OPERATION_KEY = "db.operation"
const val PERSISTENCE_UNIT_NAME_KEY = "persistenceUnit.name"
const val JDBC_URL_KEY = "jdbc.url"
const val JDBC_DRIVER_KEY = "jdbc.driver"
const val JDBC_USER_KEY = "jdbc.user"
const val JDBC_PASSWORD_KEY = "jdbc.password"
const val POST_CONSTRUCT_EXECUTE_STRATEGY = "postconstruct.executestrategy"

const val CUSTOM_CONFIGURATION_FILENAME = "needle"
const val CUSTOM_INJECTION_ANNOTATIONS_KEY = "custom.injection.annotations"
const val CUSTOM_INJECTION_PROVIDER_CLASSES_KEY = "custom.injection.provider.classes"
const val CUSTOM_INSTANCES_SUPPLIER_CLASSES_KEY = "custom.instances.supplier.classes"

internal class ConfigurationLoader(resourceName: String = CUSTOM_CONFIGURATION_FILENAME) {
  val configProperties = loadResourceAndDefault(resourceName)

  private fun loadDefaults() = mutableMapOf(
    PERSISTENCE_UNIT_NAME_KEY to "TestDataModel",
    MOCK_PROVIDER_KEY to "org.needle4k.mock.MockitoProvider",
    DB_OPERATION_KEY to "org.needle4k.db.operation.h2.H2DeleteOperation",
    JDBC_URL_KEY to "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
    JDBC_DRIVER_KEY to "org.h2.Driver",
    JDBC_USER_KEY to "",
    JDBC_PASSWORD_KEY to "",
    POST_CONSTRUCT_EXECUTE_STRATEGY to PostConstructExecuteStrategy.DEFAULT.name
  )

  private fun loadResourceAndDefault(name: String): Map<String, String> {
    val result = loadDefaults()
    val properties = Properties()
    val resource = "/$name.properties"
    val stream = loadResource(resource)

    if (stream != null) {
      stream.use { properties.load(it) }
      result.putAll(properties.asMap())
      LOG.info("Loaded Needle config from {}", resource)
    } else {
      LOG.warn("Could not load Needle config from {}, using defaults", resource)
    }

    return result
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(ConfigurationLoader::class.java)

    /**
     * Returns an input stream for reading the specified resource.
     *
     * @param resource the resource name
     * @return an input stream for reading the resource or null
     * @throws FileNotFoundException if the resource could not be found
     */
    @Throws(FileNotFoundException::class)
    fun loadResource(resource: String): InputStream? {
      val hasLeadingSlash = resource.startsWith("/")
      val stripped = if (hasLeadingSlash) resource.substring(1) else resource
      val classLoader = Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()

      return classLoader.getResourceAsStream(resource) ?: classLoader.getResourceAsStream(stripped)
    }
  }

  private fun Properties.asMap() = this.map { it.key.toString() to it.value.toString() }.toMap()
}