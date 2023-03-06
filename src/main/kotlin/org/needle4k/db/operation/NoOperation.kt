package org.needle4k.db.operation

import org.needle4k.db.DatabaseInjectorConfiguration

class NoOperation(configuration: DatabaseInjectorConfiguration) : AbstractDBOperation(configuration) {
  override fun setUpOperation() {
  }

  override fun tearDownOperation() {
  }
}