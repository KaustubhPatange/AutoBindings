package com.kpstv.processor.utils

import com.squareup.javapoet.ClassName

object Consts {
    const val adapterPrefix = "Bind"
    const val holderSuffix = "Holder"

    const val view = "view"
    const val dataSet = "dataSet"
    const val className = "className"
    const val position = "position"
    const val holder = "holder"

    val CLASSNAME_LISTADAPTER = ClassName.get(
        "androidx.recyclerview.widget",
        "ListAdapter"
    )
    val CLASSNAME_DIFFUTIL = ClassName.get(
        "androidx.recyclerview.widget",
        "DiffUtil",
        "ItemCallback"
    )
    val CLASSNAME_RECYCLERVIEW = ClassName.get(
        "androidx.recyclerview.widget",
        "RecyclerView"
    )
    val CLASSNAME_VIEWHOLDER = ClassName.get(
        CLASSNAME_RECYCLERVIEW.canonicalName(),
        "ViewHolder"
    )
    val CLASSNAME_JAVALIST = ClassName.get(
        "java.util",
        "List"
    )
    val CLASSNAME_VIEW: ClassName? = ClassName.get(
        "android.view",
        "View"
    )
    val CLASSNAME_VIEWGROUP: ClassName? = ClassName.get(
        "android.view",
        "ViewGroup"
    )

    val CLASSNAME_VIEW_ONCLICK_LISTENER = ClassName.get(
        "android.view",
        "View",
        "OnClickListener"
    )

    val CLASSNAME_VIEW_ONLONGCLICK_LISTENER = ClassName.get(
        "android.view",
        "View",
        "OnLongClickListener"
    )
}