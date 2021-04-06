package com.kpstv.processor.utils

import com.kpstv.bindings.ImageTransformationType
import com.kpstv.processor.utils.Consts.AUTOBINDINGS_INCREMENTAL
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.io.File
import java.io.IOException
import java.lang.StringBuilder
import java.nio.file.FileAlreadyExistsException
import javax.annotation.processing.FilerException
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import javax.tools.JavaFileManager
import javax.tools.StandardLocation

object Utils {
    fun write(
        packageName: String,
        typeSpec: TypeSpec,
        processingEnv: ProcessingEnvironment,
        typeElement: TypeElement? = null
    ) {
        try {
            JavaFile.builder(
                packageName,
                typeSpec
            )
                .build()
                .writeTo(processingEnv.filer)
        } catch (e: FilerException) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                e.toString()
            )
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

    fun getAppropriateSuffix(dataType: AutoGeneratorDataType): String {
        return when(dataType) {
            AutoGeneratorDataType.DATA -> Consts.converterSuffix
            AutoGeneratorDataType.LIST -> Consts.converterListSuffix
            AutoGeneratorDataType.MAP -> Consts.converterMapSuffix
            AutoGeneratorDataType.PAIR -> Consts.converterPairSuffix
        }
    }

    fun prepareJavaModelName(data: String): String {
        return if (data.contains("\\.")) {
            data.split(".").joinToString { getJavaModelName(it) }
        } else getJavaModelName(data)
    }

    fun isEnum(type: TypeMirror?, types: Types): Boolean {
        for (directSupertype in types.directSupertypes(type)) {
            val element = (directSupertype as DeclaredType).asElement() as TypeElement
            if (element.qualifiedName.contentEquals("java.lang.Enum")) {
                return true
            }
            if (isEnum(directSupertype, types)) {
                return true
            }
        }
        return false
    }

    fun disableIncrementalProcessing(processingEnv: ProcessingEnvironment): Boolean {
        return !(processingEnv.options[AUTOBINDINGS_INCREMENTAL]?.toBoolean() ?: false)
    }

    private fun getJavaModelName(item: String) =
        if (!item.startsWith("get")) "get${item.capitalize()}()" else item
}