package com.kpstv.sample_ktx

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    data class User(private val name: String)

    @Test
    fun addition_isCorrect() {
        val list = listOf(Data("v1"), Data("v2"))
       // val json = Json.encodeToString(list)
        val json = DataListConverter.toStringFromData(list)
        println(json)
    }

    @Test
    fun ko() {
        val json = Json.decodeFromString<List<Data>>("[{\"name\":\"v1\"},{\"name\":\"v2\"}]")
        println(json)
    }
}