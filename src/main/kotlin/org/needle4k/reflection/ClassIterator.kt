package org.needle4k.reflection

internal class ClassIterator(clazz: Class<*>) : Iterable<Class<*>> {
  private var currentClass: Class<*>? = clazz

  override fun iterator() = object : Iterator<Class<*>> {
    override fun hasNext() = currentClass != Any::class.java && currentClass != null

    override fun next(): Class<*> {
      val result = currentClass!!

      currentClass = result.superclass

      return result
    }
  }
}

internal fun Class<*>.toClassHierarchy() = ClassIterator(this)