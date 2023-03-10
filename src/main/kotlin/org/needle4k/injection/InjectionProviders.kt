package org.needle4k.injection

import javax.inject.Named
import javax.inject.Qualifier

/**
 *
 * Utility class for creating [InjectionProvider]s with default behavior.
 *
 * Usage:
 *
 * <pre>
 * import static org.needle4j.injection.InjectionProviders.*;
</pre> *
 *
 *
 * then call static factory methods to create providers.
 *
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Jan Galinski, Holisticon AG (jan.galinski@holisticon.de)
 */
@Suppress("unused")
object InjectionProviders {
  /**
   * InjectionProvider that provides a singleton instance of type T whenever
   * injection is required.
   *
   * @param instance the instance to return whenever assignable
   * @return InjectionProvider for instance
   */
  @JvmStatic
  fun <T> providerForInstance(instance: T): InjectionProvider<T> = DefaultInstanceInjectionProvider<T>(instance)

  /**
   * InjectionProvider that provides a singleton instance of type T for every
   * injection point that is annotated with a [Named] qualifier with
   * value "name".
   *
   * @param name     value of Named annotation
   * @param instance the instance to return whenever needed
   * @return InjectionProvider for instance
   */
  @JvmStatic
  fun <T> providerForNamedInstance(name: String, instance: T): InjectionProvider<T> =
    NamedInstanceInjectionProvider(name, instance)

  /**
   * InjectionProvider that provides a singleton instance of type T for every
   * injection point that is annotated with the given qualifier.
   *
   * @param qualifier qualifying annotation of injection point
   * @param instance  the instance to return whenever needed
   * @return InjectionProvider for instance
   */
  @JvmStatic
  fun <T> providerForQualifiedInstance(qualifier: Class<out Annotation>, instance: T): InjectionProvider<T> =
    QualifiedInstanceInjectionProvider(qualifier, instance)

  /**
   * Creates a new Set.
   *
   * @param providers vararg array of providers
   * @return set containing providers
   */
  private fun newProviderSet(vararg providers: InjectionProvider<*>) = providers.toSet()

  /**
   * Creates a new Supplier.
   *
   * @param providers vararg array of providers
   * @return new supplier
   */
  @JvmStatic
  fun supplierForInjectionProviders(vararg providers: InjectionProvider<*>) =
    supplierForInjectionProviders(newProviderSet(*providers))

  @JvmStatic
  fun supplierForInjectionProviders(providers: Set<InjectionProvider<*>>) =
    object : InjectionProviderInstancesSupplier {
      override fun get() = providers
    }

  /**
   * Creates new supplier containing all providers in a new set.
   *
   * @param suppliers vararg array of existing suppliers
   * @return new instance containing all providers
   */
  private fun mergeSuppliers(vararg suppliers: InjectionProviderInstancesSupplier) =
    object : InjectionProviderInstancesSupplier {
      override fun get() = suppliers.map { it.get() }.flatten().toSet()
    }

  /**
   * Create array of providers from given suppliers.
   *
   * @param suppliers vararg array of suppliers
   * @return array of providers for use with vararg method
   */
  @JvmStatic
  fun providersForInstancesSuppliers(vararg suppliers: InjectionProviderInstancesSupplier) =
    mergeSuppliers(*suppliers).get().toTypedArray()

  @JvmStatic
  fun providersToSet(vararg providers: InjectionProvider<*>) = providers.toSet()

  /**
   * Create array of InjectionProviders for given collection.
   *
   * @param providers providers to be contained
   * @return array of given providers
   */
  @JvmStatic
  fun providersToArray(providers: Collection<InjectionProvider<*>>) = providers.toTypedArray()

  /**
   * Base class for all instance injection providers.
   *
   * @param <T> type of instance
  </T> */
  private abstract class InstanceInjectionProvider<T>(protected val instance: T) : InjectionProvider<T> {

    /**
     * `true` when injection target is or extends/implements
     * instance type
     *
     * @param injectionTargetInformation
     * @return true when type is assignable from instance
     */
    protected fun isTargetAssignable(injectionTargetInformation: InjectionTargetInformation<*>) =
      injectionTargetInformation.injectedObjectType.isAssignableFrom(instance!!::class.java)

    protected fun isTargetQualifierPresent(
      injectionTargetInformation: InjectionTargetInformation<*>,
      qualifier: Class<out Annotation>
    ) = qualifier.isQualifier() && injectionTargetInformation.isAnnotationPresent(qualifier)

    @Suppress("UNCHECKED_CAST")
    override fun <T> getInjectedObject(injectionTargetType: Class<T>): T = instance as T
  }

  private class DefaultInstanceInjectionProvider<T>(instance: T) : InstanceInjectionProvider<T>(instance) {
    override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) =
      injectionTargetInformation.injectedObjectType

    override fun verify(injectionTargetInformation: InjectionTargetInformation<*>) =
      isTargetAssignable(injectionTargetInformation)
  }

  private open class QualifiedInstanceInjectionProvider<T>(protected val qualifier: Class<out Annotation>, instance: T) :
    InstanceInjectionProvider<T>(instance) {

    override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) =
      injectionTargetInformation.injectedObjectType.canonicalName + "#" + qualifier.name

    override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
      return (isTargetQualifierPresent(injectionTargetInformation, qualifier) && isTargetAssignable(injectionTargetInformation))
    }
  }

  private class NamedInstanceInjectionProvider<T>(private val name: String, instance: T) :
    QualifiedInstanceInjectionProvider<T>(
      Named::class.java, instance
    ) {
    override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) =
      injectionTargetInformation.injectedObjectType.canonicalName + "#" + name

    override fun verify(injectionTargetInformation: InjectionTargetInformation<*>) =
      isTargetQualifierPresent(injectionTargetInformation, qualifier)
          && (injectionTargetInformation.getAnnotation(qualifier) as Named).value == name
          && isTargetAssignable(injectionTargetInformation)
  }
}

fun Class<out Annotation>.isQualifier() = getAnnotation(Qualifier::class.java) != null