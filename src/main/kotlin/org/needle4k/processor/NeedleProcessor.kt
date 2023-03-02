package org.needle4k.processor

import org.needle4k.NeedleContext

interface NeedleProcessor {
  /**
   * @param context needle context for test class
   */
  fun process(context: NeedleContext)
}