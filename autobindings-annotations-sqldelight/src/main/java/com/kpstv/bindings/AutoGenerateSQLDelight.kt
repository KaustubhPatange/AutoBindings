package com.kpstv.bindings

/**
 * Automatically generate [SQLDelightAdapter] for SQLDelight.
 *
 * @see <a href="https://github.com/KaustubhPatange/AutoBindings/wiki/ColumnAdapter-Generation">Documentation</a>
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoGenerateSQLDelightAdapters(val using: ConverterType)