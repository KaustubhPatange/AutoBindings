package com.kpstv.bindings

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoGenerateConverter(val using: ConverterType)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoGenerateListConverter(val using: ConverterType)

enum class ConverterType {
    GSON, MOSHI, KOTLIN_SERIALIZATION
}