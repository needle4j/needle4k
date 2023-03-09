package org.needle4k.db.operation.hsql

import org.needle4k.db.DatabaseInjectorConfiguration
import org.needle4k.db.operation.AbstractDeleteOperation
import java.sql.SQLException
import java.sql.Statement

/**
 * Delete everything from the DB: This cannot be done with the JPA, because the
 * order of deletion matters. Instead, we directly use a JDBC connection.
 */
open class HSQLDeleteOperation constructor(configuration: DatabaseInjectorConfiguration) : AbstractDeleteOperation(configuration) {
  @Throws(SQLException::class)
  override fun setReferentialIntegrity(enable: Boolean, statement: Statement) {
    val databaseMajorVersion = statement.connection.metaData.databaseMajorVersion
    val referentialIntegrity = enable.toString().uppercase()
    val command = if (databaseMajorVersion < 2) "SET REFERENTIAL_INTEGRITY " else "SET DATABASE REFERENTIAL INTEGRITY "

    statement.execute(command + referentialIntegrity)
  }
}