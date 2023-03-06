package org.needle4k.db.operation

import java.sql.SQLException

/**
 * Database operations before and after test execution.
 */
interface DBOperation {
  /**
   * Execute the database operation in test setup.
   *
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  fun setUpOperation()

  /**
   * Execute the database operation in test tear down.
   *
   * @throws SQLException if a database access error occurs
   */
  @Throws(SQLException::class)
  fun tearDownOperation()
}