package com.kpstv.library_annotations

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

/**
 * Annotate [OnBind] method with this to automatically load image from the url.
 *
 * Code example (Kotlin)
 * ```
 * @GlideLoadArray(
 *   GlideLoad(R.id.item_big_image, "parameter-name",...)
 *   ...
 * )
 * @OnBind(...)
 * fun bind(...): Unit
 * ```
 *
 * @see [GlideLoad.data]
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class GlideLoadArray(vararg val glideLoad: GlideLoad)


/**
 * @see [GlideLoadArray]
 */
@Target(AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
annotation class GlideLoad(
    @IdRes val itemId: Int,
    /**
     * If suppose `Model` is your data class which is defined below &
     * `imageUrl` is the string you want to load.
     * ```
     * data class Model(val name: String, val imageUrl: String)
     * ```
     * Then set [data] as,
     * ```
     * data = imageUrl
     * ```
     *
     * For nested types you can put period and go on.
     */
    val data: String,
    @DrawableRes
    val placeHolderRes: Int = -1,
    @DrawableRes
    val errorRes: Int = -1,
    val cachingStrategyImage: ImageCacheStrategyType = ImageCacheStrategyType.ALL,
    val transformationType: ImageTransformationType = ImageTransformationType.NONE
)

enum class ImageCacheStrategyType {
    ALL, NONE, AUTOMATIC, DATA, RESOURCE
}

enum class ImageTransformationType {
    NONE, CENTER_CROP, CENTER_INSIDE, CIRCLE_CROP, FIT_CENTER
}