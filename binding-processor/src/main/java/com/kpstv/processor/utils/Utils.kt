package com.kpstv.processor.utils

import com.kpstv.library_annotations.ImageTransformationType
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import java.lang.StringBuilder
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

object Utils {
    fun write(
        packageName: String,
        typeSpec: TypeSpec,
        typeElement: TypeElement,
        processingEnv: ProcessingEnvironment
    ) {
        try {
            JavaFile.builder(
                packageName,
                typeSpec
            )
                .build()
                .writeTo(processingEnv.filer)
        } catch (e: IOException) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                e.toString(),
                typeElement
            )
        }
    }

    fun applyTransformationType(builder: StringBuilder, transformationType: ImageTransformationType){
        when(transformationType){
            ImageTransformationType.CENTER_CROP -> {
                builder.append(".centerCrop()")
            }
            ImageTransformationType.CENTER_INSIDE -> {
                builder.append(".centerInside()")
            }
            ImageTransformationType.CIRCLE_CROP -> {
                builder.append(".circleCrop()")
            }
            ImageTransformationType.FIT_CENTER -> {
                builder.append(".fitCenter()")
            }
            else->{}
        }
    }

    fun prepareJavaModelName(data: String): String {
        return if (data.contains("\\.")) {
            data.split(".").joinToString { getJavaModelName(it) }
        } else getJavaModelName(data)
    }

    private fun getJavaModelName(item: String) =
        if (!item.startsWith("get")) "get${item.capitalize()}()" else item
}