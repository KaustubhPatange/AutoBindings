package androidx.room

/**
 * Blob @TypeConverter annotation.
 */
@Target(AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
annotation class TypeConverter