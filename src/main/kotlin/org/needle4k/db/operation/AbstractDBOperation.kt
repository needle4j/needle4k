package org.needle4k.db.operation

import org.needle4k.configuration.ConfigurationLoader
import org.needle4k.db.JPAInjectorConfiguration
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.sql.*

/**
 * An abstract implementation of [DBOperation] with common jdbc
 * operations.
 */
abstract class AbstractDBOperation(val configuration: JPAInjectorConfiguration) : DBOperation {
  protected val logger = LoggerFactory.getLogger(javaClass)!!

  /**
   * Returns the names of all tables in the database by using
   * [DatabaseMetaData].
   *
   * @param connection the jdbc connection object
   * @return a [List] of all table names
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  fun getTableNames(connection: Connection): List<String> {
    val tables: MutableList<String> = ArrayList()

    connection.metaData.getTables(null, null, "%", arrayOf("TABLE")).use {
      while (it.next()) {
        val schema = it.getString("TABLE_SCHEM")

        if (schema == null || !ignoredSchemas.contains(schema.uppercase())) {
          val tableName = it.getString("TABLE_NAME")

          tables.add(tableName)
        }
      }
    }
    return tables
  }

  @Suppress("MemberVisibilityCanBePrivate")
  protected val ignoredSchemas = mutableListOf("INFORMATION_SCHEMA")

  @Throws(SQLException::class)
  private fun executeScript(script: BufferedReader, statement: Statement) {
    var lineNo: Long = 0
    val sql = StringBuilder()
    var line: String?

    try {
      while (script.readLine().also { line = it } != null) {
        lineNo++

        val trimmedLine = line!!.trim { it <= ' ' }

        if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("--") && !trimmedLine.startsWith("//")) {
          if (trimmedLine.startsWith("/*")) {
            while (script.readLine().also { line = it } != null) {
              if (line!!.trim { it <= ' ' }.endsWith("*/")) {
                logger.debug("Ignoring $line")
                break
              }
            }
          } else {
            sql.append(trimmedLine)

            if (trimmedLine.endsWith(";")) {
              val sqlStatement = sql.toString()

              logger.info(sqlStatement)
              statement.execute(sqlStatement)
              sql.setLength(0)
            }
          }
        }
      }
    } catch (e: Exception) {
      throw SQLException("Error during import script execution at line $lineNo", e)
    }
  }

  /**
   * Execute the given sql script.
   *
   * @param filename  the filename of the sql script
   * @param statement the [Statement] to be used for executing a SQL
   * statement.
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  fun executeScript(filename: String, statement: Statement) {
    val message = "Executing sql script: $filename"
    logger.info(message)

    try {
      ConfigurationLoader.loadResource(filename)?.use {
        BufferedReader(InputStreamReader(it)).use { reader ->
          executeScript(reader, statement)
        }
      } ?: logger.warn("File $filename not found")
    } catch (e: IOException) {
      logger.error(message, e)
      throw SQLException(message, e)
    }
  }
}