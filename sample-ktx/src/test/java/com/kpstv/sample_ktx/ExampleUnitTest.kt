package com.kpstv.sample_ktx

import com.google.gson.Gson
import com.kpstv.bindings.AutoBinding
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private lateinit var vio: Map<String, String>
//    @Test
//    fun addition_isCorrect() {
//        val user1 = User("John", mapOf(1 to Clip("p1", true), 2 to Clip("p2", false)))
//        val user2 = User("Amanda", mapOf(3 to Clip("p11", true), 4 to Clip("p22", false)))
//        val type = AutoBinding.inferType<List<User>>()
//
//        //  val type = object : TypeToken<List<User>>() {}.type
////        val serializer = kotlinx.serialization.KSerializer()
////        Reflection.
//
//        val types = Types.newParameterizedType(type)
//        val json = Moshi.Builder().build().adapter<List<User>>(type).toJson(listOf(user1, user2))
//        val gson = Gson()
//
//        println(types)
//
//        //   val toJson = gson.toJson(mapOf(1 to "One", 2 to "two"), type)
//        // println(DataConverter.toStringFromData(Data("djddi",false)))
//    }
    @Test
    fun test() {
        // List<User> ->  KSerializer $this$cast$iv$iv$iv = SerializersKt.serializer($this$serializer$iv$iv, Reflection.typeOf(List.class, KTypeProjection.Companion.invariant(Reflection.typeOf(User.class))));
        // Map<String, User>
        val user1 = User("John", mapOf(1 to Clip("p1", true), 2 to Clip("p2", false)))
        val string = Json.encodeToString(mapOf(1 to user1, 2 to user1))
    }
}