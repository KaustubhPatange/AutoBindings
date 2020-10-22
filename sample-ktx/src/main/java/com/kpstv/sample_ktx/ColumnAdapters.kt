package com.kpstv.sample_ktx

import com.kpstv.bindings.AutoGenerateSQLDelightAdapters
import com.kpstv.bindings.Column2DAdapter
import com.kpstv.bindings.ColumnAdapter
import com.kpstv.bindings.ConverterType

@AutoGenerateSQLDelightAdapters(
    using = ConverterType.KOTLIN_SERIALIZATION,
    adapters = [
        ColumnAdapter(name = "visibility", source = Data::class),
        ColumnAdapter(name = "clip", source = User::class),
    ],
    listAdapters = [
        ColumnAdapter(name = "string", source = String::class)
    ],
    mapAdapters = [
        Column2DAdapter(name = "main", keySource = String::class, valueSource = User::class)
    ]
)
@Suppress("unused")
class ColumnAdapters