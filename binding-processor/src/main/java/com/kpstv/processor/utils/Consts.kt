package com.kpstv.processor.utils

import com.squareup.javapoet.ClassName

object Consts {
    const val adapterPrefix = "Bind"
    const val holderSuffix = "Holder"

    const val dataSet = "dataSet"
    const val className = "className"

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
}