package com.kpstv.bindings

import kotlin.reflect.KClass

/**
 * Automatically generate [TypeConverter] for a Room Database.
 *
 * @see <a href="https://github.com/KaustubhPatange/AutoBindings/wiki/TypeConverter-Generation">Documentation</a>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoGenerateConverter(val using: ConverterType)

/**
 * Automatically generate List<T> [TypeConverter] for a Room Database.
 *
 * @see <a href="https://github.com/KaustubhPatange/AutoBindings/wiki/TypeConverter-Generation">Documentation</a>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoGenerateListConverter(val using: ConverterType)

/**
 * Automatically generate Map<K, V> [TypeConverter] for a Room Database.
 * Parameter [keyClass] is your K & the annotated data class is your V in Map<K, V>
 *
 * @see <a href="https://github.com/KaustubhPatange/AutoBindings/wiki/TypeConverter-Generation">Documentation</a>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoGenerateMapConverter(val keyClass: KClass<*>, val using: ConverterType)

/**
 * Automatically generate Pair<K, V> [TypeConverter] for a Room Database.
 * Parameter [keyClass] is your K & the annotated data class is your V in Pair<K, V>
 *
 * @see <a href="https://github.com/KaustubhPatange/AutoBindings/wiki/TypeConverter-Generation">Documentation</a>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoGeneratePairConverter(val keyClass: KClass<*>, val using: ConverterType)

/**
 * Automatically generate [SQLDelightAdapter] for SQLDelight.
 *
 * @see <a href="https://github.com/KaustubhPatange/AutoBindings/wiki/ColumnAdapter-Generation">Documentation</a>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoGenerateSQLDelightAdapters(val using: ConverterType)

/**
 * Creates a [SQLDelightAdapter]
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class SQLDelightAdapter

enum class ConverterType {
    GSON, MOSHI, KOTLIN_SERIALIZATION
}