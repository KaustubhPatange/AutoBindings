package com.kpstv.lint.utils

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin

object Stubs {
    val RECYCLERVIEW_ANNOTATION: TestFile = kotlin(
        """
            package com.kpstv.bindings
            import kotlin.reflect.KClass
            
            @Target(AnnotationTarget.CLASS)
            @Retention(AnnotationRetention.SOURCE)
            annotation class RecyclerViewAdapter(val dataSetType: KClass<*> = Any::class)
    """
    ).indented()
    val RECYCLERVIEWLIST_ANNOTATION: TestFile = kotlin(
        """
            package com.kpstv.bindings
            import kotlin.reflect.KClass
            
            @Target(AnnotationTarget.CLASS)
            @Retention(AnnotationRetention.SOURCE)
            annotation class RecyclerViewListAdapter(val dataSetType: KClass<*> = Any::class)
    """
    ).indented()
    val ONBIND_ANNOTATION = kotlin(
        """
            package com.kpstv.bindings
            import androidx.annotation.LayoutRes
            
            @Target(AnnotationTarget.FUNCTION)
            @Retention(AnnotationRetention.SOURCE)
            annotation class OnBind(@LayoutRes val layoutId: Int, val viewType: Int = 0)
        """
    ).indented()
    val ONCLICK_ANNOTATION = kotlin(
        """
            package com.kpstv.bindings
            
            @Target(AnnotationTarget.FUNCTION)
            @Retention(AnnotationRetention.SOURCE)
            annotation class OnClick(
                val itemId: Int,
                val setInViewHolder: Boolean = true,
                val viewType: Int = 0
            )
        """
    ).indented()
    val ONLONGCLICK_ANNOTATION = kotlin(
        """
            package com.kpstv.bindings
            
            @Target(AnnotationTarget.FUNCTION)
            @Retention(AnnotationRetention.SOURCE)
            annotation class OnLongClick(
                val itemId: Int,
                val setInViewHolder: Boolean = true,
                val viewType: Int = 0
            )
        """
    ).indented()
    val ITEMVIEWTYPE_ANNOTATION = kotlin(
        """
            package com.kpstv.bindings
            
            @Target(AnnotationTarget.FUNCTION)
            @Retention(AnnotationRetention.SOURCE)
            annotation class ItemViewType
        """
    ).indented()
    val DIFFITEMSAME_ANNOTATION = kotlin(
        """
            package com.kpstv.bindings
            
            @Target(AnnotationTarget.FUNCTION)
            @Retention(AnnotationRetention.SOURCE)
            annotation class DiffItemSame
        """
    ).indented()
    val DIFFCONTENTSAME_ANNOTATION = kotlin(
        """
            package com.kpstv.bindings
            
            @Target(AnnotationTarget.FUNCTION)
            @Retention(AnnotationRetention.SOURCE)
            annotation class DiffContentSame
        """
    ).indented()

    val CLASS_VIEW = kotlin(
        """
         package android.view
         class View { }
     """
    ).indented()

    val CLASS_CONTEXT = kotlin(
        """
            package android.content
            
            class Context { }
        """
    ).indented()

    val CLASS_KOTLIN_SERIALIZABLE = kotlin(
        """
            package kotlinx.serialization
            annotation class Serializable
        """
    )

    val CLASS_CONVERTER_TYPE = kotlin(
        """
            package com.kpstv.bindings
            
            enum class ConverterType {
                GSON, MOSHI, KOTLIN_SERIALIZATION
            } 
        """
    ).indented()

    val CLASS_JSONCLASS = kotlin(
        """
        package com.squareup.moshi
        annotation class JsonClass(generateAdapter: Boolean = false)
        """
    )

    val CONVERTER_ANNOTATIONS = kotlin(
     """
            package com.kpstv.bindings
            
            import com.kpstv.bindings.ConverterType
            
            @Target(AnnotationTarget.CLASS)
            @Retention(AnnotationRetention.SOURCE)
            annotation class AutoGenerateConverter(val using: ConverterType)
     """
    ).indented()

    val LISTCONVERTER_ANNOTATIONS = kotlin(
        """
            package com.kpstv.bindings
            
            import com.kpstv.bindings.ConverterType
            
            @Target(AnnotationTarget.CLASS)
            @Retention(AnnotationRetention.SOURCE)
            annotation class AutoGenerateListConverter(val using: ConverterType)
        """
    ).indented()
}