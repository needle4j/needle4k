package org.needle4k.configuration.db.operation

data class JdbcConfiguration(
  val jdbcUrl: String,
  val jdbcUser: String,
  val jdbcPassword: String,
  val jdbcDriver: String
)