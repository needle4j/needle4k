package org.needle4k.db.operation

import org.needle4k.configuration.ConfigurationLoader
import org.needle4k.configuration.NeedleConfiguration
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.sql.*

/**
 * An abstract implementation of [DBOperation] with common jdbc
 * operations.
 */
abstract class AbstractDBOperation(private val needleConfiguration: NeedleConfiguration) : DBOperation {
  private lateinit var conn: Connection

  /**
   * Close the connection to the database.
   *
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  protected fun closeConnection() {
    if (this::conn.isInitialized && !conn.isClosed) {
      conn.close()
    }
  }

  /**
   * Commits the current transaction.
   *
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  protected fun commit() {
    if (this::conn.isInitialized && !conn.isClosed) {
      conn.commit()
    }
  }

  /**
   * Revoke the current transaction.
   *
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  protected fun rollback() {
    if (this::conn.isInitialized && !conn.isClosed) {
      conn.rollback()
    }
  }

  /**
   * Returns the names of all tables in the database by using
   * [DatabaseMetaData].
   *
   * @param connection the jdbc connection object
   * @return a [List] of all table names
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  protected fun getTableNames(connection: Connection): List<String> {
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
    var line: String

    try {
      while (script.readLine().also { line = it } != null) {
        lineNo++

        val trimmedLine = line.trim { it <= ' ' }

        if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("--") && !trimmedLine.startsWith("//")) {
          if (trimmedLine.startsWith("/*")) {
            while (script.readLine().also { line = it } != null) {
              if (line.trim { it <= ' ' }.endsWith("*/")) {
                LOG.debug("ignore $line")
                break
              }
            }
          } else {
            sql.append(trimmedLine)

            if (trimmedLine.endsWith(";")) {
              var sqlStatement = sql.toString()
              sqlStatement = sqlStatement.substring(0, sqlStatement.length - 1)

              LOG.info(sqlStatement)
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
  protected fun executeScript(filename: String, statement: Statement) {
    val message = "Executing sql script: $filename"
    LOG.info(message)

    try {
      ConfigurationLoader.loadResource(filename)?.use {
        BufferedReader(InputStreamReader(it)).use { reader ->
          executeScript(reader, statement)
        }
      }
    } catch (e: IOException) {
      LOG.error(message, e)
      throw SQLException(message, e)
    }
  }

  /**
   * Returns the sql connection object. If there is no connection a new connection is established.
   *
   * @return the sql connection object
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  protected fun getConnection(): Connection {
    if (!this::conn.isInitialized || conn.isClosed) {
      val configuration = needleConfiguration.jdbcConfiguration

      needleConfiguration.reflectionHelper.lookupClass(Driver::class.java, configuration.jdbcDriver)
        ?: throw IllegalStateException("JDBC driver ${configuration.jdbcDriver} not found")

      conn = DriverManager.getConnection(configuration.jdbcUrl, configuration.jdbcUser, configuration.jdbcPassword)
      conn.autoCommit = false
    }

    return conn
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(AbstractDBOperation::class.java)
  }
}