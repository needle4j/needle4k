package org.needle4k.injection

interface InjectionVerifier {
    /**
     * Verifies the injection target.
     *
     * @param injectionTargetInformation information about the injection point
     * @return true, if the provided object is injectable to the given injection
     * information, otherwise false.
     */
    fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean
}