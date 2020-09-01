package com.kpstv.lint

import org.jetbrains.uast.UClass

enum class Language {
    JAVA,
    KOTLIN
}

fun UClass.language(): Language? {
    return when (language.displayName) {
        "JAVA" -> Language.JAVA
        "kotlin" -> Language.KOTLIN
        else -> null
    }
}

object Utils {
    fun getSimpleName(qualifiedName: String?): String? {
        return qualifiedName?.substring(qualifiedName.lastIndexOf(".") + 1, qualifiedName.length)
    }
}