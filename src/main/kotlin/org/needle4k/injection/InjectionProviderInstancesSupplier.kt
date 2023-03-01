package org.needle4k.injection

/**
 * [Supplies](http://javadocs.techempower.com/jdk18/api/java/util/function/Supplier.html) a Set of InjectionProvider instances.
 *
 * @author Jan Galinski, Holisticon AG
 */
interface InjectionProviderInstancesSupplier {
  /**
   * [Supplies](http://javadocs.techempower.com/jdk18/api/java/util/function/Supplier.html) a Set of InjectionProvider instances.
   *
   * @return InjectionProviders
   */
  fun get(): Set<InjectionProvider<*>>
}