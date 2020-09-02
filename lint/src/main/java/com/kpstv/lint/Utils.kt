package com.kpstv.lint

import com.intellij.psi.PsiClass
import com.kpstv.lint.detectors.RecyclerViewDetector
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UMethod

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

    fun findAnnotationValue(clazz: PsiClass?, name: String) =
        if (clazz != null && clazz.hasAnnotation(RecyclerViewDetector.ANNOTATION_RECYCLERVIEWLIST)) {
            clazz.getAnnotation(RecyclerViewDetector.ANNOTATION_RECYCLERVIEWLIST)
                ?.findAttributeValue(name)?.text ?: "Any"
        } else "Any"
}