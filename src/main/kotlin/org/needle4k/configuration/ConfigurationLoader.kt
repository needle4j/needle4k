package org.needle4k.configuration

import org.needle4k.db.operation.hsql.HSQLDeleteOperation
import org.needle4k.mock.MockitoProvider
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

const val MOCK_PROVIDER_KEY = "mock.provider"
const val DB_OPERATION_KEY = "db.operation"
const val PERSISTENCE_UNIT_NAME_KEY = "persistenceUnit.name"
const val POST_CONSTRUCT_EXECUTE_STRATEGY = "postconstruct.executestrategy"

const val CUSTOM_CONFIGURATION_FILENAME = "needle"
const val CUSTOM_INJECTION_ANNOTATIONS_KEY = "custom.injection.annotations"
const val CUSTOM_INJECTION_PROVIDER_CLASSES_KEY = "custom.injection.provider.classes"
const val CUSTOM_INSTANCES_SUPPLIER_CLASSES_KEY = "custom.instances.supplier.classes"

internal class ConfigurationLoader(resourceName: String = CUSTOM_CONFIGURATION_FILENAME) {
  val configProperties = loadResourceAndDefault(resourceName)

  private fun loadDefaults() = mutableMapOf(
    PERSISTENCE_UNIT_NAME_KEY to "TestDataModel",
    MOCK_PROVIDER_KEY to MockitoProvider::class.qualifiedName!!,
    DB_OPERATION_KEY to HSQLDeleteOperation::class.qualifiedName!!,
    POST_CONSTRUCT_EXECUTE_STRATEGY to PostConstructExecuteStrategy.DEFAULT.name
  )

  private fun loadResourceAndDefault(name: String): MutableMap<String, String> {
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