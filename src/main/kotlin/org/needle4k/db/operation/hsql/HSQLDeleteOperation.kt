package org.needle4k.db.operation.hsql

import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.operation.AbstractDeleteOperation
import java.sql.SQLException
import java.sql.Statement

/**
 * Delete everything from the DB: This cannot be done with the JPA, because the
 * order of deletion matters. Instead, we directly use a JDBC connection.
 */
class HSQLDeleteOperation @JvmOverloads constructor(needleConfiguration: NeedleConfiguration = DefaultNeedleConfiguration.INSTANCE)
  : AbstractDeleteOperation(needleConfiguration) {
  @Throws(SQLException::class)
  override fun setReferentialIntegrity(enable: Boolean, statement: Statement) {
    val databaseMajorVersion = statement.connection.metaData.databaseMajorVersion
    val referentialIntegrity = enable.toString().uppercase()
    val command = if (databaseMajorVersion < 2) "SET REFERENTIAL_INTEGRITY " else "SET DATABASE REFERENTIAL INTEGRITY "

    statement.execute(command + referentialIntegrity)
  }
}