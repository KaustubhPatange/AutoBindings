package com.kpstv.processor.generators

import com.kpstv.library_annotations.Bind
import com.kpstv.processor.utils.Consts
import com.kpstv.processor.utils.getElementFromAnnotation
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

object BindViewGenerator {
    fun generateOnBindViewHolder(
        typeElement: TypeElement,
        viewHolderClassName: ClassName
    ): MethodSpec {

        val bindMethod = typeElement.getElementFromAnnotation<Bind>()

        return MethodSpec.methodBuilder("onBindViewHolder")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addStatement("${Consts.className}.${bindMethod?.simpleName}(holder.itemView, ${Consts.dataSet}.get(position), position)")
            .addParameter(viewHolderClassName, "holder")
            .addParameter(TypeName.INT, "position")
            .build()
    }

    fun generateOnBindViewListHolder(
        typeElement: TypeElement,
        viewHolderClassName: ClassName
    ): MethodSpec {

        val bindMethod = typeElement.getElementFromAnnotation<Bind>()

        return MethodSpec.methodBuilder("onBindViewHolder")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addStatement("${Consts.className}.${bindMethod?.simpleName}(holder.itemView, getItem(position), position)")
            .addParameter(viewHolderClassName, "holder")
            .addParameter(TypeName.INT, "position")
            .build()
    }
}