package com.kpstv.library_annotations

/**
 * One of the method of [DiffUtil.ItemCallback]
 *
 * Code example (Kotlin)
 * ```
 * @DiffItemSame
 * fun diffItemSame(oldItem: Data, newItemSame: Data): Boolean
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DiffItemSame

/**
 * One of the method of [DiffUtil.ItemCallback]
 *
 * Code example (Kotlin)
 * ```
 * @DiffContentSame
 * fun diffContentSame(oldItem: Data, newItemSame: Data): Boolean
 * ```
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class DiffContentSame