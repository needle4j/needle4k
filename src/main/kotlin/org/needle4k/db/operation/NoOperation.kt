package org.needle4k.db.operation

import org.needle4k.db.JPAInjectorConfiguration

class NoOperation(configuration: JPAInjectorConfiguration) : AbstractDBOperation(configuration) {
  override fun setUpOperation() {
  }

  override fun tearDownOperation() {
  }
}