package com.kpstv.processor.generators

import androidx.annotation.NonNull
import com.kpstv.processor.utils.Consts
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier

object ViewHolderGenerator {
    fun generateOnCreateViewHolder(
        viewHolderClassName: ClassName,
        codeBlock: CodeBlock
    ): MethodSpec {
        return MethodSpec.methodBuilder("onCreateViewHolder")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addAnnotation(NonNull::class.java)
            .addParameter(Consts.CLASSNAME_VIEWGROUP, "parent")
            .addParameter(TypeName.INT, "viewType")
            .returns(viewHolderClassName)
            .addCode(codeBlock)
            .addStatement("return null")
            .build()
    }

    fun createViewHolder(viewHolderClassName: ClassName): TypeSpec {
        return TypeSpec.classBuilder(viewHolderClassName.simpleName())
            .addModifiers(Modifier.STATIC)
            .superclass(Consts.CLASSNAME_VIEWHOLDER)
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(Consts.CLASSNAME_VIEW, Consts.view)
                    .addStatement("super(${Consts.view})")
                    .build()
            ).build()
    }
}