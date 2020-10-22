package com.kpstv.sample_ktx

import com.kpstv.bindings.*
import com.squareup.sqldelight.ColumnAdapter

@AutoGenerateSQLDelightAdapters(using = ConverterType.KOTLIN_SERIALIZATION)
interface SQLDelightAdapters {
    @SQLDelightAdapter
    fun dataConverter(): ColumnAdapter<Data, String>
    @SQLDelightAdapter
    fun dataListConverter(): ColumnAdapter<List<Data>, String>
    @SQLDelightAdapter
    fun dataMapConverter(): ColumnAdapter<Map<String, Data>, String>
}