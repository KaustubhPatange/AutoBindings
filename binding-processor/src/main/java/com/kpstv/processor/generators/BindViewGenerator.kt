package com.kpstv.processor.generators

import com.kpstv.library_annotations.GlideLoadArray
import com.kpstv.library_annotations.OnBind
import com.kpstv.processor.utils.*
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import java.lang.StringBuilder
import java.util.*
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

object BindViewGenerator {
    fun generateOnBindViewHolder(
        typeElement: TypeElement,
        viewHolderClassName: ClassName
    ): MethodSpec {

        val bindMethod = typeElement.getElementFromAnnotation<OnBind>()

        return MethodSpec.methodBuilder("onBindViewHolder")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addStatement("${Consts.className}.${bindMethod?.simpleName}(${Consts.holder}.itemView, ${Consts.dataSet}.get(${Consts.position}), ${Consts.position})")
            .addParameter(viewHolderClassName, Consts.holder, Modifier.FINAL)
            .addParameter(TypeName.INT, Consts.position, Modifier.FINAL)
            .also { generateBindAnnotations(it, bindMethod) }
            .also { ClickUtils.generateClickListener(it, typeElement, BINDTYPE.BIND) }
            .build()
    }

    fun generateOnBindViewListHolder(
        typeElement: TypeElement,
        viewHolderClassName: ClassName
    ): MethodSpec {

        val bindMethod = typeElement.getElementFromAnnotation<OnBind>()

        return MethodSpec.methodBuilder("onBindViewHolder")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addStatement("${Consts.className}.${bindMethod?.simpleName}(${Consts.holder}.itemView, getItem(${Consts.position}), ${Consts.position})")
            .addParameter(viewHolderClassName, Consts.holder, Modifier.FINAL)
            .addParameter(TypeName.INT, Consts.position, Modifier.FINAL)
            .also { generateBindAnnotations(it, bindMethod, true) }
            .also { ClickUtils.generateClickListener(it, typeElement, BINDTYPE.BIND, true) }
            .build()
    }

    private fun generateBindAnnotations(
        methodSpecBuilder: MethodSpec.Builder,
        bindElement: ExecutableElement?,
        isListAdapter: Boolean = false
    ) {
        val glideLoadArrayAnnotation = bindElement?.getAnnotation(GlideLoadArray::class.java)
        glideLoadArrayAnnotation?.glideLoad?.forEach {
            val glideBlock = StringBuilder().apply {
                append("com.bumptech.glide.Glide.with(${Consts.holder}.itemView.getContext())")
                append(".load(getItem(${Consts.position}).${Utils.prepareJavaModelName(it.data)})")
                append(".diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.${it.cachingStrategyImage.name})")
                Utils.applyTransformationType(this, it.transformationType)
                if (it.errorRes != -1) {
                    append(".error(${it.errorRes})")
                }
                if (it.placeHolderRes != -1) {
                    append(".placeholder(${it.placeHolderRes})")
                }
                append(".into((${Consts.CLASSNAME_IMAGEVIEW.canonicalName()}) ${Consts.holder}.itemView.findViewById(${it.itemId}))")
            }
            methodSpecBuilder.apply {
                addStatement(glideBlock.toString())
            }
        }
    }
}