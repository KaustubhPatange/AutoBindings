package com.kpstv.processor.generators

import com.kpstv.library_annotations.Bind
import com.kpstv.processor.utils.BINDTYPE
import com.kpstv.processor.utils.ClickUtils
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
            .addStatement("${Consts.className}.${bindMethod?.simpleName}(${Consts.holder}.itemView, ${Consts.dataSet}.get(${Consts.position}), ${Consts.position})")
            .addParameter(viewHolderClassName, Consts.holder, Modifier.FINAL)
            .addParameter(TypeName.INT, Consts.position, Modifier.FINAL)
            .also { ClickUtils.generateClickListener(it, typeElement, BINDTYPE.BIND) }
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
            .addStatement("${Consts.className}.${bindMethod?.simpleName}(${Consts.holder}.itemView, getItem(${Consts.position}), ${Consts.position})")
            .addParameter(viewHolderClassName, Consts.holder, Modifier.FINAL)
            .addParameter(TypeName.INT, Consts.position, Modifier.FINAL)
            .also { ClickUtils.generateClickListener(it, typeElement,  BINDTYPE.BIND, true) }
            .build()
    }
}