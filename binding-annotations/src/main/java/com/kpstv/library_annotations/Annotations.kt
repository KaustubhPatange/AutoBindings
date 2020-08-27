package com.kpstv.library_annotations

import androidx.annotation.IdRes
import androidx.annotation.IntDef

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnBind

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

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BindVisibilityArray(vararg val bindVisibility: BindVisibility)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BindVisibility(@IdRes val itemId: Int, val predicate: String, val visibility: Int)

/*
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BindVisibility(@IdRes val itemId: Int, val predicate: String, val visibility: Int)*/
