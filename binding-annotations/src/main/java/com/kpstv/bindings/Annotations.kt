package com.kpstv.bindings

import androidx.annotation.LayoutRes

/**
 * Code example (Kotlin)
 * ```
 * @Bind(...)
 * fun bind(view: View, item: Data, position: Int): Unit
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnBind(@LayoutRes val layoutId: Int, val viewType: Int = 0)

/**
 * Works as getViewItemType in Adapter.
 *
 * Code example (Kotlin)
 * ```
 * @ItemViewType
 * fun viewType(position: Int): Int
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ItemViewType
