package com.kpstv.processor.generators

import com.kpstv.bindings.DiffContentSame
import com.kpstv.bindings.DiffItemSame
import com.kpstv.processor.utils.Consts
import com.kpstv.processor.utils.getElementFromAnnotation
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

object DiffUtilGenerator {
    fun generateDiffUtil(typeElement: TypeElement, dataClassTypeName: TypeName): TypeSpec {

        val diffItemSame = typeElement.getElementFromAnnotation<DiffItemSame>()
        val diffContentSame = typeElement.getElementFromAnnotation<DiffContentSame>()

        return TypeSpec.anonymousClassBuilder("")
            .addSuperinterface(
                ParameterizedTypeName.get(Consts.CLASSNAME_DIFFUTIL, dataClassTypeName)
            )
            .addMethod(
                MethodSpec.methodBuilder("areItemsTheSame")
                    .returns(TypeName.BOOLEAN)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override::class.java)
                    .addParameter(dataClassTypeName, "oldItem")
                    .addParameter(dataClassTypeName, "newItem")
                    .addStatement("return ${Consts.className}.${diffItemSame?.simpleName}(oldItem, newItem)")
                    .build()
            )
            .addMethod(
                MethodSpec.methodBuilder("areContentsTheSame")
                    .returns(TypeName.BOOLEAN)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override::class.java)
                    .addParameter(dataClassTypeName, "oldItem")
                    .addParameter(dataClassTypeName, "newItem")
                    .addStatement("return ${Consts.className}.${diffContentSame?.simpleName}(oldItem, newItem)")
                    .build()
            )
            .build()
    }
}