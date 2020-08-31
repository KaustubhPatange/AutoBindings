package com.kpstv.lint.utils

import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin

object Stubs {
    val RECYCLERVIEW_ANNOTATION: TestFile = kotlin(
        """
            package com.kpstv.library_annotations
            import kotlin.reflect.KClass
            
            @Target(AnnotationTarget.CLASS)
            @Retention(AnnotationRetention.SOURCE)
            annotation class RecyclerViewAdapter(val dataSetType: KClass<*> = Any::class)
    """
    ).indented()
    val RECYCLERVIEWLIST_ANNOTATION: TestFile = kotlin(
        """
            package com.kpstv.library_annotations
            import kotlin.reflect.KClass
            
            @Target(AnnotationTarget.CLASS)
            @Retention(AnnotationRetention.SOURCE)
            annotation class RecyclerViewListAdapter(val dataSetType: KClass<*> = Any::class)
    """
    ).indented()
    val ONBIND_ANNOTATION = kotlin(
        """
            package com.kpstv.library_annotations
            import androidx.annotation.LayoutRes
            
            @Target(AnnotationTarget.FUNCTION)
            @Retention(AnnotationRetention.SOURCE)
            annotation class OnBind(@LayoutRes val layoutId: Int, val viewType: Int = 0)
        """
    ).indented()
    val ONCLICK_ANNOTATION = kotlin(
        """
            package com.kpstv.library_annotations
            
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
            package com.kpstv.library_annotations
            
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
            package com.kpstv.library_annotations
            
            @Target(AnnotationTarget.FUNCTION)
            @Retention(AnnotationRetention.SOURCE)
            annotation class ItemViewType
        """
    ).indented()
    val DIFFITEMSAME_ANNOTATION = kotlin(
        """
            package com.kpstv.library_annotations
            
            @Target(AnnotationTarget.FUNCTION)
            @Retention(AnnotationRetention.SOURCE)
            annotation class DiffItemSame
        """
    ).indented()
    val DIFFCONTENTSAME_ANNOTATION = kotlin(
        """
            package com.kpstv.library_annotations
            
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
}