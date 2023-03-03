package org.needle4k.reflection

import org.needle4k.configuration.NeedleConfiguration
import org.slf4j.LoggerFactory
import java.lang.reflect.*
import java.util.*

class ReflectionUtil(private val configuration: NeedleConfiguration) {
  fun getAllFieldsWithSupportedAnnotation(clazz: Class<*>) =
    clazz.allDeclaredFields().filter { configuration.injectionAnnotationRegistry.isRegistered(*it.annotations) }

  fun getAllFieldsWithAnnotation(clazz: Class<*>, annotation: Class<out Annotation>) =
    clazz.allDeclaredFields().filter { it.isAnnotationPresent(annotation) }

  fun getAllMethodsWithAnnotation(clazz: Class<*>, annotation: Class<out Annotation>) =
    clazz.allDeclaredMethods().filter { it.isAnnotationPresent(annotation) }

  fun getAllAnnotatedFields(clazz: Class<*>): Map<Class<out Annotation>, List<Field>> =
    clazz.allDeclaredFields().map { it to it.annotations.map { a -> a.annotationClass.java }.toList() }
      .map { it.second.map { clazz -> it.first to clazz } }.flatten()
      .groupBy({ pair -> pair.second }, { pair -> pair.first })

  fun getAllFieldsAssignableFrom(assignableType: Class<*>, clazz: Class<*>) =
    clazz.allDeclaredFields().filter { it.type.isAssignableFrom(assignableType) }

  fun getAllFieldsWithAnnotation(instance: Any, annotation: Class<out Annotation>): List<Field> {
    return getAllFieldsWithAnnotation(instance.javaClass, annotation)
  }

  fun getAllFields(clazz: Class<*>) = clazz.allDeclaredFields()

  /**
   * @param clazz object
   * @return list of method objects
   * @see Class.getMethods
   */
  fun getMethods(clazz: Class<*>) = clazz.methods.toList()

  /**
   * Changing the value of a given field.
   *
   * @param instance    -- target object of injection
   * @param clazz     -- type of argument object
   * @param fieldName -- name of field whose value is to be set
   * @param value     -- object that is injected
   */
  @Throws(NoSuchFieldException::class)
  fun setFieldValue(instance: Any, clazz: Class<*>, fieldName: String, value: Any?) {
    val field = clazz.getDeclaredField(fieldName)

    try {
      setField(field, instance, value)
    } catch (e: Exception) {
      LOG.error(e.message, e)
    }
  }

  /**
   * Changing the value of a given field.
   *
   * @param instance    -- target object of injection
   * @param fieldName -- name of field whose value is to be set
   * @param value     -- object that is injected
   */
  fun setFieldValue(instance: Any, fieldName: String, value: Any) {
    val field = instance.javaClass.allDeclaredFields().firstOrNull { it.name.equals(fieldName) }
      ?: throw IllegalArgumentException("Could not find field $fieldName")

    setField(field, instance, value)
  }

  /**
   * Get the value of a given field on a given object via reflection.
   *
   * @param instance   -- target object of field access
   * @param clazz     -- type of argument object
   * @param fieldName -- name of the field
   * @return -- the value of the represented field in object; primitive values
   * are wrapped in an appropriate object before being returned
   */
  fun getFieldValue(instance: Any, clazz: Class<*>, fieldName: String): Any? {
    return try {
      val field = clazz.getDeclaredField(fieldName)
      getFieldValue(instance, field)
    } catch (e: Exception) {
      throw IllegalArgumentException("Could not get field value: $fieldName", e)
    }
  }

  /**
   * Get the value of a given field on a given object via reflection.
   *
   * @param `instance -- target object of field access
   * @param field  -- target field
   * @return -- the value of the represented field in object; primitive values
   * are wrapped in an appropriate object before being returned
   */
  fun getFieldValue(instance: Any, field: Field): Any? {
    return try {
      if (!field.canAccess(instance)) {
        field.isAccessible = true
      }

      field.get(instance)
    } catch (e: Exception) {
      throw IllegalArgumentException("Could not get field value: $field", e)
    }
  }

  /**
   * Get the value of a given field on a given object via reflection.
   *
   * @param object    -- target object of field access
   * @param fieldName -- name of the field
   * @return -- the value of the represented field in object; primitive values
   * are wrapped in an appropriate object before being returned
   */
  @Suppress("unused")
  fun getFieldValue(`object`: Any, fieldName: String): Any? {
    return getFieldValue(`object`, `object`.javaClass, fieldName)
  }

  /**
   * Invoke a given method with given arguments on a given object via
   * reflection.
   *
   * @param instance     -- target object of invocation
   * @param clazz      -- type of argument object
   * @param methodName -- name of method to be invoked
   * @param arguments  -- arguments for method invocation
   * @return -- method object to which invocation is actually dispatched
   * @throws Exception - operation exception
   */
  @Throws(Exception::class)
  private fun invokeMethod(instance: Any, clazz: Class<*>, methodName: String, vararg arguments: Any?): Any? {
    val method = clazz.allDeclaredMethods().firstOrNull {
      val parameterTypes = it.parameterTypes
      it.name == methodName && parameterTypes.size == arguments.size && checkArguments(parameterTypes, *arguments)
    } ?: throw IllegalArgumentException("Could not find matching method $methodName in $clazz")

    return invokeMethod(method, instance, *arguments)
  }

  @Throws(Exception::class)
  fun invokeMethod(method: Method, instance: Any, vararg arguments: Any?): Any? {
    return try {
      if (!method.canAccess(instance)) {
        method.isAccessible = true
      }

      method.invoke(instance, *arguments)
    } catch (exc: Exception) {
      LOG.warn("Error invoking method: " + method.name, exc)
      val cause = exc.cause

      throw if (cause is Exception) {
        cause
      } else {
        exc
      }
    }
  }

  @Throws(NoSuchMethodException::class)
  fun getMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method =
    clazz.allDeclaredMethods().filter { it.name == methodName }.map {
      try {
        it.declaringClass.getDeclaredMethod(methodName, *parameterTypes)
      } catch (e: Exception) {
        null
      }
    }.first() ?: throw NoSuchMethodException(methodName)

  /**
   * Invoke a given method with given arguments on a given object via
   * reflection.
   *
   * @param instance     -- target object of invocation
   * @param methodName -- name of method to be invoked
   * @param arguments  -- arguments for method invocation
   * @return -- method object to which invocation is actually dispatched
   * @throws Exception - exception
   */
  @Throws(Exception::class)
  fun invokeMethod(instance: Any, methodName: String, vararg arguments: Any?): Any? {
    return invokeMethod(instance, instance.javaClass, methodName, *arguments)
  }

  /**
   * Returns the `Class` object associated with the class or
   * interface with the given string name.
   *
   * @param className the fully qualified name of the desired class.
   * @return `Class` or null
   */
  fun forName(className: String): Class<*>? {
    return try {
      Class.forName(className)
    } catch (e: ClassNotFoundException) {
      LOG.warn("Class $className not found")
      null
    }
  }

  @Throws(Exception::class)
  fun setField(field: Field, target: Any, value: Any?) {
    if (!field.canAccess(target)) {
      field.isAccessible = true
    }

    field.set(target, value)
  }

  @Throws(Exception::class)
  fun setField(fieldName: String, target: Any, value: Any?) {
    val field = getField(target.javaClass, fieldName)
    setField(field, target, value)
  }

  @Throws(Exception::class)
  fun getField(clazz: Class<*>, fieldName: String): Field = clazz.allDeclaredFields().first { it.name == fieldName }

  @Throws(Exception::class)
  fun <T> createInstance(clazz: Class<T>, vararg parameter: Any): T {
    val parameterTypes: Array<Class<*>> = parameter.map { it.javaClass }.toTypedArray()
    val constructor = clazz.getDeclaredConstructor(*parameterTypes)

    return constructor.newInstance(*parameter)
  }

  /**
   * @param type      - base class
   * @param className - fully qualified class name
   * @return class object
   * @throws ClassNotFoundException - ClassNotFoundException
   */
  @Suppress("UNCHECKED_CAST")
  fun <T> lookupClass(type: Class<T>, className: String): Class<T>? {
    val clazz = forName(className)

    return when {
      clazz == null -> null
      type.isAssignableFrom(clazz) -> clazz as Class<T>
      else -> throw IllegalArgumentException("$className cannot be assigned to $type")
    }
  }

  private fun checkArguments(parameterTypes: Array<Class<*>>, vararg arguments: Any?): Boolean {
    for (i in arguments.indices) {
      val parameterClass = parameterTypes[i]
      val argumentClass = arguments[i]?.javaClass ?: parameterClass

      if (!parameterClass.isAssignableFrom(argumentClass) && !checkPrimitiveArguments(parameterClass, argumentClass)) {
        return false
      }
    }

    return true
  }

  private fun checkPrimitiveArguments(parameterClass: Class<*>, argumentClass: Class<*>): Boolean {
    var result = false

    for ((key, value) in PRIMITIVES) {
      result = result || parameterClass == key && argumentClass == value
    }

    return result
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(ReflectionUtil::class.java)

    private val PRIMITIVES = mapOf(
      Int::class.javaPrimitiveType to java.lang.Integer::class.java,
      Double::class.javaPrimitiveType to java.lang.Double::class.java,
      Boolean::class.javaPrimitiveType to java.lang.Boolean::class.java,
      Long::class.javaPrimitiveType to java.lang.Long::class.java,
      Float::class.javaPrimitiveType to java.lang.Float::class.java,
      Char::class.javaPrimitiveType to java.lang.Character::class.java,
      Short::class.javaPrimitiveType to java.lang.Short::class.java,
      Byte::class.javaPrimitiveType to java.lang.Byte::class.java
    )
  }

  private fun Class<*>.allDeclaredFields() = toClassHierarchy().toList().map { it.declaredFields.toList() }.flatten()
  private fun Class<*>.allDeclaredMethods() = toClassHierarchy().toList().map { it.declaredMethods.toList() }.flatten()
  private fun Class<*>.toClassHierarchy() = ClassIterator(this)
}