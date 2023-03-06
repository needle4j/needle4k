package org.needle4k.configuration.db.operation.h2

import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.configuration.db.operation.AbstractDeleteOperation
import java.sql.SQLException
import java.sql.Statement

/**
 * Delete everything from the DB: This cannot be done with the JPA, because the
 * order of deletion matters. Instead we directly use a JDBC connection.
 */
class H2DeleteOperation(needleConfiguration: NeedleConfiguration) : AbstractDeleteOperation(needleConfiguration) {
  @Throws(SQLException::class)
  override fun setReferentialIntegrity(enable: Boolean, statement: Statement) {
    val command = "SET REFERENTIAL_INTEGRITY " + enable.toString().uppercase()
    statement.execute(command)
  }
}