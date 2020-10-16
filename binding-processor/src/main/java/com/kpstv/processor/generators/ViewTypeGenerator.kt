package com.kpstv.processor.generators

import com.kpstv.bindings.ItemViewType
import com.kpstv.processor.utils.Consts
import com.kpstv.processor.utils.getElementFromAnnotation
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

object ViewTypeGenerator {
    fun generateItemViewType(typeElement: TypeElement): MethodSpec {
        val annotation = typeElement.getElementFromAnnotation<ItemViewType>()

        val methodSpec = MethodSpec.methodBuilder("getItemViewType")
            .addParameter(TypeName.INT, Consts.position)
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.INT)

        if (annotation != null)
            methodSpec.addStatement("return ${Consts.className}.${annotation.simpleName}(${Consts.position})")
        else methodSpec.addStatement("return 0")

        return methodSpec.build()
    }
}