package com.kpstv.bindings

import androidx.annotation.IdRes

/**
 * Code example (Kotlin)
 * ```
 * @OnClick(...)
 * fun onClick(context: Context, item: Data, position: Int): Unit
 * ```
 *
 * @param setInViewHolder If set to false listener will be generated in onBindViewHolder()
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnClick(
    @IdRes val itemId: Int,
    val setInViewHolder: Boolean = true,
    val viewType: Int = 0
)

/**
 * Code example (Kotlin)
 * ```
 * @OnLongClick(...)
 * fun onClick(context: Context, item: Data, position: Int): Unit
 * ```
 *
 * @param setInViewHolder If set to false listener will be generated in onBindViewHolder()
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnLongClick(
    @IdRes val itemId: Int,
    val setInViewHolder: Boolean = true,
    val viewType: Int = 0
)
