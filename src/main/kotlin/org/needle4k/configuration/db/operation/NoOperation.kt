package org.needle4k.configuration.db.operation

import org.needle4k.configuration.NeedleConfiguration

class NoOperation(needleConfiguration: NeedleConfiguration) : AbstractDBOperation(needleConfiguration) {
  override fun setUpOperation() {
  }

  override fun tearDownOperation() {
  }
}