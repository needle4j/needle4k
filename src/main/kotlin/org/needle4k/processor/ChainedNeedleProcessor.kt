package org.needle4k.processor

import org.needle4k.NeedleContext

/**
 * [NeedleProcessor] that calls chain of processors.
 */
class ChainedNeedleProcessor(vararg processors: NeedleProcessor) : NeedleProcessor {
  private val processors = ArrayList(processors.toList())

  fun addProcessor(vararg processors: NeedleProcessor) {
    this.processors.addAll(processors.toList())
  }

  override fun process(context: NeedleContext) {
    for (processor in processors) {
      processor.process(context)
    }
  }
}