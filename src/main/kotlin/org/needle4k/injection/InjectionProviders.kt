@file:JvmName("InjectionUtil")

package org.needle4k.injection

import org.needle4k.reflection.ReflectionUtil.invokeMethod
import org.needle4k.reflection.ReflectionUtil.lookupClass

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
  fun <T : Any> providerForInstance(instance: T): InjectionProvider<T> = DefaultInstanceInjectionProvider(instance)

  /**
   * InjectionProvider that provides a singleton instance of type T for every
   * injection point that is annotated with a @Named qualifier with
   * value "name".
   *
   * @param name     value of Named annotation
   * @param instance the instance to return whenever needed
   * @return InjectionProvider for instance
   */
  @JvmStatic
  fun <T : Any> providerForNamedInstance(name: String, instance: T): InjectionProvider<T> =
    NamedInstanceInjectionProvider(namedAnnotation, name, instance)

  /**
   * InjectionProvider that provides a singleton instance of type T for every
   * injection point that is annotated with the given qualifier.
   *
   * @param qualifier qualifying annotation of injection point
   * @param instance  the instance to return whenever needed
   * @return InjectionProvider for instance
   */
  @JvmStatic
  fun <T : Any> providerForQualifiedInstance(qualifier: Class<out Annotation>, instance: T): InjectionProvider<T> =
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
  private abstract class InstanceInjectionProvider<T : Any>(protected val instance: T) : InjectionProvider<T> {

    /**
     * `true` when injection target is or extends/implements
     * instance type
     *
     * @param injectionTargetInformation
     * @return true when type is assignable from instance
     */
    protected fun isTargetAssignable(injectionTargetInformation: InjectionTargetInformation<*>) =
      injectionTargetInformation.injectedObjectType.isAssignableFrom(instance::class.java)

    protected fun isTargetQualifierPresent(
      injectionTargetInformation: InjectionTargetInformation<*>,
      qualifier: Class<out Annotation>
    ) = qualifier.isQualifier() && injectionTargetInformation.isAnnotationPresent(qualifier)

    @Suppress("UNCHECKED_CAST")
    override fun getInjectedObject(injectionTargetType: Class<*>) = instance
  }

  private class DefaultInstanceInjectionProvider<T : Any>(instance: T) : InstanceInjectionProvider<T>(instance) {
    override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) =
      injectionTargetInformation.injectedObjectType

    override fun verify(injectionTargetInformation: InjectionTargetInformation<*>) =
      isTargetAssignable(injectionTargetInformation)
  }

  private open class QualifiedInstanceInjectionProvider<T : Any>(
    protected val annotationClass: Class<out Annotation>, instance: T
  ) :
    InstanceInjectionProvider<T>(instance) {

    override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) =
      injectionTargetInformation.injectedObjectType.canonicalName + "#" + annotationClass.name

    override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
      return (isTargetQualifierPresent(injectionTargetInformation, annotationClass) && isTargetAssignable(
        injectionTargetInformation
      ))
    }
  }

  private class NamedInstanceInjectionProvider<T : Any>(named: Class<out Annotation>, private val name: String, instance: T) :
    QualifiedInstanceInjectionProvider<T>(named, instance) {
    override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) =
      injectionTargetInformation.injectedObjectType.canonicalName + "#" + name

    override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
      val annotationInstance = injectionTargetInformation.getAnnotation(annotationClass)

      return if (annotationInstance != null) {
        val namedValue = invokeMethod(annotationInstance, "value")

        isTargetQualifierPresent(injectionTargetInformation, annotationClass)
            && namedValue == name
            && isTargetAssignable(injectionTargetInformation)
      } else {
        false
      }
    }
  }

  private val qualifierAnnotation: Class<out Annotation> by lazy {
    lookupClass(Annotation::class.java, "javax.inject.Qualifier")
      ?: lookupClass(Annotation::class.java, "jakarta.inject.Qualifier")
      ?: throw IllegalStateException("Cannot find @Qualifier interface on classpath! Please add CDI dependencies to your project.")
  }

  private val namedAnnotation: Class<out Annotation> by lazy {
    lookupClass(Annotation::class.java, "javax.inject.Named")
      ?: lookupClass(Annotation::class.java, "jakarta.inject.Named")
      ?: throw IllegalStateException("Cannot find @Named interface on classpath! Please add CDI dependencies to your project.")
  }

  @JvmStatic
  fun Class<out Annotation>.isQualifier() = getAnnotation(qualifierAnnotation) != null
}

