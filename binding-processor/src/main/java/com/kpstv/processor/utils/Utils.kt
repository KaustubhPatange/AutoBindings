package com.kpstv.processor.utils

import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.io.IOException
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
            true
        } catch (e: IOException) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.ERROR,
                e.toString(),
                typeElement
            )
        }
    }
}