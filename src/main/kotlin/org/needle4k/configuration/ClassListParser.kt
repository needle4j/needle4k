package org.needle4k.configuration

/**
 * Function to lookup classes. Expects FQN classnames separated by comma.
 */
@Suppress("UNCHECKED_CAST")
internal class ClassListParser(private val needleConfiguration: NeedleConfiguration) {
  fun <T> lookup(key: String): Set<Class<T>> {
    val classesList = needleConfiguration.configurationProperties[key] ?: ""

    return classesList.split(",").mapNotNull { needleConfiguration.reflectionHelper.forName(it.trim()) as Class<T>? }.toSet()
  }
}