package com.kpstv.processor.utils

import com.kpstv.library_annotations.OnClick
import com.kpstv.library_annotations.OnLongClick
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

enum class BINDTYPE {
    BIND,
    VIEWHOLDER
}

object ClickUtils {
    private const val position = "${Consts.holder}.getAdapterPosition()"
    private const val contextStatement = "${Consts.holder}.itemView.getContext()"
    fun generateClickListener(
        typeElement: TypeElement,
        bindType: BINDTYPE,
        listAdapter: Boolean = false,
        viewType: Int
    ): CodeBlock {
        val codeBlock = CodeBlock.builder()
        typeElement.enclosedElements.forEach { element ->
            val onClickAnnotation = element.getAnnotation(OnClick::class.java)
            if (onClickAnnotation != null
                && shouldImplement(onClickAnnotation.setInViewHolder, bindType)
                && viewType == onClickAnnotation.viewType
            ) {
                val listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(Consts.CLASSNAME_VIEW_ONCLICK_LISTENER)
                    .addMethod(
                        MethodSpec.methodBuilder("onClick")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(Consts.CLASSNAME_VIEW, Consts.view)
                            .addStatement(generateClassStatement(listAdapter, element))
                            .build()
                    )
                    .build()

                val listenerBuilder = StringBuilder().apply {
                    append("${Consts.holder}.itemView.findViewById(${onClickAnnotation.itemId}).setOnClickListener(${listener})")
                }
                codeBlock.addStatement(listenerBuilder.toString())
            }

            val onLongClickAnnotation = element.getAnnotation(OnLongClick::class.java)
            if (onLongClickAnnotation != null
                && shouldImplement(onLongClickAnnotation.setInViewHolder, bindType)
                && viewType == onLongClickAnnotation.viewType
            ) {
                val listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(Consts.CLASSNAME_VIEW_ONLONGCLICK_LISTENER)
                    .addMethod(
                        MethodSpec.methodBuilder("onLongClick")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.BOOLEAN)
                            .addParameter(Consts.CLASSNAME_VIEW, Consts.view)
                            .addStatement(generateClassStatement(listAdapter, element))
                            .addStatement("return true")
                            .build()
                    )
                    .build()
                val listenerBuilder = StringBuilder().apply {
                    append("${Consts.holder}.itemView.findViewById(${onLongClickAnnotation.itemId}).setOnLongClickListener(${listener})")
                }
                codeBlock.addStatement(listenerBuilder.toString())
            }
        }

        return codeBlock.build()
    }

    private fun generateClassStatement(listAdapter: Boolean, element: Element) =
        if (listAdapter)
            "${Consts.className}.${element.simpleName}(${contextStatement}, getItem(${position}), ${position})"
        else "${Consts.className}.${element.simpleName}(${contextStatement}, ${Consts.dataSet}.get(${position}), ${position})"


    private fun shouldImplement(setInViewHolder: Boolean, bindType: BINDTYPE) =
        ((setInViewHolder && bindType == BINDTYPE.VIEWHOLDER) || (!setInViewHolder && bindType == BINDTYPE.BIND))
}