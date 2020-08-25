package com.kpstv.processor.generators

import com.kpstv.processor.utils.Consts
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier

object ItemCountGenerator {
    fun generateGetItemCountMethod(): MethodSpec {
        return MethodSpec.methodBuilder("getItemCount")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .returns(TypeName.INT)
            .addStatement("return ${Consts.dataSet}.size()")
            .build()
    }
}