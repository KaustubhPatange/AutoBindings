package com.kpstv.sample_ktx

import com.kpstv.bindings.*
import com.squareup.sqldelight.ColumnAdapter

@AutoGenerateSQLDelightAdapters(using = ConverterType.KOTLIN_SERIALIZATION)
interface SQLDelightAdapters {
    fun tagsConverter(): ColumnAdapter<List<String>, String>
}