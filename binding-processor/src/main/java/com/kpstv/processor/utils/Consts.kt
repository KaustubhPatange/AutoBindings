package com.kpstv.processor.utils

import com.squareup.javapoet.ClassName

object Consts {
    const val adapterPrefix = "Bind"
    const val holderSuffix = "Holder"
    const val converterSuffix = "Converter"
    const val converterListSuffix = "ListConverter"

    const val toConvertMethod = "toStringFrom"
    const val fromConvertMethod = "fromStringTo"

    const val view = "view"
    const val dataSet = "dataSet"
    const val className = "className"
    const val position = "position"
    const val holder = "holder"
    const val converterName = "model"

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
    val CLASSNAME_IMAGEVIEW = ClassName.get(
        "android.widget",
        "ImageView"
    )

    val CLASSNAME_TYPECONVERTER = ClassName.get(
        "androidx.room",
        "TypeConverter"
    )

    val CLASSNAME_GSON = ClassName.get(
        "com.google.gson",
        "Gson"
    )

    val ClASSNAME_MOSHI = ClassName.get(
        "com.squareup.moshi",
        "Moshi"
    )

    val CLASSNAME_JSONADAPTER = ClassName.get(
        "com.squareup.moshi",
        "JsonAdapter"
    )

    val CLASSNAME_TYPE = ClassName.get(
        "java.lang.reflect",
        "Type"
    )

    val CLASSNAME_TYPETOKEN = ClassName.get(
        "com.google.gson.reflect",
        "TypeToken"
    )

    val CLASSNAME_LIST = ClassName.get(
        "java.util",
        "List"
    )

    val CLASSNAME_KSERIALIZER = ClassName.get(
        "kotlinx.serialization",
        "KSerializer"
    )

    val CLASSNAME_SERIALIZERSKT = ClassName.get(
        "kotlinx.serialization",
        "SerializersKt"
    )

    val CLASSANAME_KX_JSON = ClassName.get(
        "kotlinx.serialization.json",
        "Json"
    )

    val CLASSNAME_KX_REFLECTION = ClassName.get(
        "kotlin.jvm.internal",
        "Reflection"
    )
}