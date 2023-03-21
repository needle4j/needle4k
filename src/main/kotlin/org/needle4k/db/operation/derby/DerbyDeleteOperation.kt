package org.needle4k.db.operation.derby

import org.needle4k.db.JPAInjectorConfiguration
import org.needle4k.db.operation.AbstractDeleteOperation
import java.sql.SQLException
import java.sql.Statement

/**
 * Derby does not support this, unfortunately.
 */
@Suppress("unused")
open class DerbyDeleteOperation constructor(configuration: JPAInjectorConfiguration)
  : AbstractDeleteOperation(configuration) {
  @Throws(SQLException::class)
  override fun setReferentialIntegrity(enable: Boolean, statement: Statement) {
  }
}