package org.needle4k.db.operation

import org.hibernate.dialect.Database

data class JdbcConfiguration(
  val jdbcUrl: String,
  val jdbcUser: String,
  val jdbcPassword: String,
  val jdbcDriver: String
) {
  val databaseType: Database
    get() = when {
      jdbcUrl.contains("h2") -> Database.H2
      jdbcUrl.contains("hsqldb") -> Database.HSQL
      jdbcUrl.contains("derby") -> Database.DERBY
      else -> throw IllegalStateException("Cannot resolve database dialect")
    }
}