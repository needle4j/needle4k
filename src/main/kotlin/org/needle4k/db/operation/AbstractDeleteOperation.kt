package org.needle4k.db.operation

import org.needle4k.configuration.NeedleConfiguration
import org.slf4j.LoggerFactory
import java.sql.SQLException
import java.sql.Statement

/**
 * Delete everything from the DB: This cannot be done with the JPA, because the
 * order of deletion matters. Instead we directly use a JDBC connection.
 */
abstract class AbstractDeleteOperation(needleConfiguration: NeedleConfiguration) : AbstractDBOperation(needleConfiguration) {
  /**
   * {@inheritDoc} No operation implementation.
   */
  override fun setUpOperation() {}

  /**
   * {@inheritDoc}. Delete all data from all tables given by
   * [.getTableNames].
   *
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  override fun tearDownOperation() {
    try {
      getConnection().use {
        it.createStatement().use { statement ->
          val tableNames = getTableNames(it)
          disableReferentialIntegrity(statement)
          deleteContent(tableNames, statement)
          enableReferentialIntegrity(statement)
          commit()
        }
      }
    } catch (e: SQLException) {
      LOG.error(e.message, e)

      try {
        rollback()
      } catch (e1: SQLException) {
        LOG.error(e1.message, e1)
      }

      throw e
    }
  }

  /**
   * Disables the referential constraints of the database, e.g foreign keys.
   *
   * @throws SQLException - if a database access error occurs
   */
  @Throws(SQLException::class)
  fun disableReferentialIntegrity(statement: Statement) {
    setReferentialIntegrity(false, statement)
  }

  /**
   * Enables the referential constraints of the database, e.g foreign keys.
   *
   * @throws SQLException - if a database access error occurs
   */
  @Throws(SQLException::class)
  fun enableReferentialIntegrity(statement: Statement) {
    setReferentialIntegrity(true, statement)
  }

  @Throws(SQLException::class)
  protected abstract fun setReferentialIntegrity(enable: Boolean, statement: Statement)

  /**
   * Deletes all contents from the given tables.
   *
   * @param tables    a [List] of table names who are to be deleted.
   * @param statement the [Statement] to be used for executing a SQL
   * statement.
   * @throws SQLException - if a database access error occurs
   */
  @Throws(SQLException::class)
  protected fun deleteContent(tables: List<String>, statement: Statement) {
    val tempTables = ArrayList(tables)

    // Loop until all data is deleted: we don't know the correct DROP
    // order, so we have to retry upon failure
    while (tempTables.isNotEmpty()) {
      val sizeBefore = tempTables.size
      val iterator = tempTables.listIterator()

      while (iterator.hasNext()) {
        val table = iterator.next()

        try {
          statement.executeUpdate("DELETE FROM $table")
          iterator.remove()
        } catch (exc: SQLException) {
          LOG.warn("Ignored exception: " + exc.message + ". WILL RETRY.")
        }
      }

      if (tempTables.size == sizeBefore) {
        throw AssertionError("unable to clean tables $tempTables")
      }
    }
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(AbstractDeleteOperation::class.java)
  }
}