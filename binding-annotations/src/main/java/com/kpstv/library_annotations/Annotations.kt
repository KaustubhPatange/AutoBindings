package com.kpstv.library_annotations

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

/*
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BindVisibilityArray(vararg val bindVisibility: BindVisibility)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BindVisibility(@IdRes val itemId: Int, val predicate: String, val visibility: Int)*/
