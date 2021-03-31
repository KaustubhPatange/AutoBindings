package com.kpstv.sample_ktx.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kpstv.bindings.AutoGenerateConverter
import com.kpstv.bindings.AutoGeneratePairConverter
import com.kpstv.bindings.ConverterType
import com.kpstv.samplektx.data.DataDomain
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

/**
 * POJO class
 */
@Serializable
@Entity(tableName = "table_data")
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

enum class Category {
    FIRST, SECOND, THIRD
}

@Entity(tableName = "table_testdata")
@AutoGenerateConverter(using = ConverterType.GSON)
data class TestDataClass(val name: String, val category: Category) {
    @PrimaryKey(autoGenerate = true)
    var primaryKey: Int = 0
}