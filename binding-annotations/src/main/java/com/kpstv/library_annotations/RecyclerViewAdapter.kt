package com.kpstv.library_annotations

import kotlin.reflect.KClass

/**
 * Automatically generates standard RecyclerView Adapter.
 *
 * Code example (Kotlin)
 * ```
 * @RecyclerViewAdapter(POJO::class.java)
 * class TestAdapter {
 *  ...
 * }
 * ```
 *
 * This will generate `BindTestAdapter` class which can be used with
 * RecyclerView.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class RecyclerViewAdapter(val dataSetType: KClass<*> = Any::class)