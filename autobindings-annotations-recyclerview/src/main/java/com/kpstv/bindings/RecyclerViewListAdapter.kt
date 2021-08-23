package com.kpstv.bindings

import kotlin.reflect.KClass

/**
 * Automatically generates modern RecyclerView Adapter.
 *
 * Code example (Kotlin)
 * ```
 * @RecyclerViewListAdapter(POJO::class.java)
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
annotation class RecyclerViewListAdapter(val dataSetType: KClass<*>)