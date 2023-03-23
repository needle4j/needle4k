package org.needle4k

import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.JPAInjectionProvider
import org.needle4k.db.JPAInjectorConfiguration

interface NeedleSession {
  val needleInjector: NeedleInjector
  val needleConfiguration: NeedleConfiguration
  val needleContext: NeedleContext
  val jpaInjectionProvider: JPAInjectionProvider
  val jpaInjectorConfiguration: JPAInjectorConfiguration
}