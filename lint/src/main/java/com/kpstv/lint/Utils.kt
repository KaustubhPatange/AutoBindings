package com.kpstv.lint

object Utils {
    fun getSimpleName(qualifiedName: String?): String? {
        return qualifiedName?.substring(qualifiedName.lastIndexOf(".")+1, qualifiedName.length)
    }
}