package com.kpstv.library_annotations

import androidx.annotation.IdRes

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Bind

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnClick(
    @IdRes val itemId: Int,
    /** Set this to false to generate onClick listener in onBindViewHolder() */
    val setInViewHolder: Boolean = true
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnLongClick(
    @IdRes val itemId: Int,
    /** Set this to false to generate onClick listener in onBindViewHolder() */
    val setInViewHolder: Boolean = true
)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DiffItemSame

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DiffContentSame