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
 * Automatically generate [ColumnAdapter] for SQLDelight.
 *
 * @see <a href="https://github.com/KaustubhPatange/AutoBindings/wiki/ColumnAdapter-Generation">Documentation</a>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoGenerateSQLDelightAdapters(
    val using: ConverterType,
    val adapters: Array<ColumnAdapter> = [],
    val listAdapters: Array<ColumnAdapter> = [],
    val mapAdapters: Array<Column2DAdapter> = [],
    val pairAdapters: Array<Column2DAdapter> = []
)

/**
 * Creates a [ColumnAdapter] for the [source]
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ColumnAdapter(
    val name: String,
    val source: KClass<*>
)

/**
 * Creates a 2D [ColumnAdapter] based on [keySource] & [valueSource]
 * Typically used to create Map, Pair Adapters
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Column2DAdapter(
    val name: String,
    val keySource: KClass<*>,
    val valueSource: KClass<*>,
)

enum class ConverterType {
    GSON, MOSHI, KOTLIN_SERIALIZATION
}