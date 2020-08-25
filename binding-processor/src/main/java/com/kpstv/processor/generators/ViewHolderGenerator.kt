package com.kpstv.processor.generators

import com.kpstv.processor.utils.Consts
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

object ViewHolderGenerator {
    fun generateOnCreateViewHolder(
        viewHolderClassName: ClassName,
        layoutId: Int
    ): MethodSpec {
        return MethodSpec.methodBuilder("onCreateViewHolder")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addParameter(Consts.CLASSNAME_VIEWGROUP, "parent")
            .addParameter(TypeName.INT, "viewType")
            .addStatement("return new ${viewHolderClassName.simpleName()}(android.view.LayoutInflater.from(parent.getContext()).inflate(${layoutId}, parent, false))")
            .returns(viewHolderClassName)
            .build()
    }

    fun createViewHolder(viewHolderClassName: ClassName): TypeSpec {
        return TypeSpec.classBuilder(viewHolderClassName.simpleName())
            .addModifiers(Modifier.STATIC)
            .superclass(Consts.CLASSNAME_VIEWHOLDER)
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Consts.CLASSNAME_VIEW, "view")
                    .addStatement("super(view)")
                    .build()
            ).build()
    }
}