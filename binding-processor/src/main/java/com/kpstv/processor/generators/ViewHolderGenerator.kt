package com.kpstv.processor.generators

import androidx.annotation.NonNull
import com.kpstv.processor.utils.BINDTYPE
import com.kpstv.processor.utils.ClickUtils
import com.kpstv.processor.utils.Consts
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

object ViewHolderGenerator {
    fun generateOnCreateViewHolder(
        typeElement: TypeElement,
        viewHolderClassName: ClassName,
        layoutId: Int,
        isListAdapter: Boolean = false
    ): MethodSpec {

        return MethodSpec.methodBuilder("onCreateViewHolder")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addAnnotation(NonNull::class.java)
            .addParameter(Consts.CLASSNAME_VIEWGROUP, "parent")
            .addParameter(TypeName.INT, "viewType")
            .addStatement("final ${viewHolderClassName.simpleName()} holder = new ${viewHolderClassName.simpleName()}(android.view.LayoutInflater.from(parent.getContext()).inflate(${layoutId}, parent, false))")
            .also { ClickUtils.generateClickListener(it, typeElement, BINDTYPE.VIEWHOLDER, isListAdapter) }
            .addStatement("return holder")
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
                    .addParameter(Consts.CLASSNAME_VIEW, Consts.view)
                    .addStatement("super(${Consts.view})")
                    .build()
            ).build()
    }
}