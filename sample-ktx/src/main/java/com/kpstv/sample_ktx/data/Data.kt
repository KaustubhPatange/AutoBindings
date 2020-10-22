package com.kpstv.sample_ktx.data

import com.kpstv.bindings.AutoGeneratePairConverter
import com.kpstv.bindings.ConverterType
import com.kpstv.samplektx.data.DataDomain
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

/**
 * POJO class
 */
@Serializable
@JsonClass(generateAdapter = true)
@AutoGeneratePairConverter(keyClass = String::class, using = ConverterType.MOSHI)
data class Data(val name: String, val imageUrl: String, val tags: List<String>)

fun Data.mapToDomain(): DataDomain {
    return DataDomain(
        id = 0,
        name = name,
        imageUrl = imageUrl,
        tags = tags
    )
}

fun List<Data>.mapToDomain(): List<DataDomain> {
    return map { it.mapToDomain() }
}