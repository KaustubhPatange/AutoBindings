package com.kpstv.bindings

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class AutoBinding {
    companion object {
        @JvmStatic
        inline fun<reified T> inferType(): Type {
            return object : TypeToken<T>() {}.type
        }
    }
}