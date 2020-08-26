package com.kpstv.processor.generators

import com.kpstv.library_annotations.Bind
import com.kpstv.library_annotations.OnClick
import com.kpstv.library_annotations.OnLongClick
import com.kpstv.processor.utils.Consts
import com.kpstv.processor.utils.getElementFromAnnotation
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
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
            .addParameter(viewHolderClassName, "holder", Modifier.FINAL)
            .addParameter(TypeName.INT, "position", Modifier.FINAL)
            .also { generateClickListener(it, typeElement) }
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
            .addParameter(viewHolderClassName, "holder", Modifier.FINAL)
            .addParameter(TypeName.INT, "position", Modifier.FINAL)
            .also { generateClickListener(it, typeElement, true) }
            .build()
    }

    private fun generateClickListener(
        methodSpecBuilder: MethodSpec.Builder,
        typeElement: TypeElement,
        listAdapter: Boolean = false
    ) {
        val contextStatement = "holder.itemView.getContext()"
        typeElement.enclosedElements.forEach { element ->
            val onClickAnnotation = element.getAnnotation(OnClick::class.java)
            if (onClickAnnotation != null) {
                val listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(Consts.CLASSNAME_VIEW_ONCLICK_LISTENER)
                    .addMethod(
                        MethodSpec.methodBuilder("onClick")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.VOID)
                            .addParameter(Consts.CLASSNAME_VIEW, "view")
                            .addStatement(
                                if (listAdapter)
                                    "${Consts.className}.${element.simpleName}(${contextStatement}, getItem(position), position)"
                                else "${Consts.className}.${element.simpleName}(${contextStatement}, ${Consts.dataSet}.get(position), position)"
                            )
                            .build()
                    )
                    .build()
                methodSpecBuilder.addStatement("holder.itemView.findViewById(${onClickAnnotation.itemId}).setOnClickListener(${listener})")
            }

            val onLongClickAnnotation = element.getAnnotation(OnLongClick::class.java)
            if (onLongClickAnnotation != null) {
                val listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(Consts.CLASSNAME_VIEW_ONLONGCLICK_LISTENER)
                    .addMethod(
                        MethodSpec.methodBuilder("onLongClick")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(TypeName.BOOLEAN)
                            .addParameter(Consts.CLASSNAME_VIEW, "view")
                            .addStatement(
                                if (listAdapter)
                                    "${Consts.className}.${element.simpleName}(${contextStatement}, getItem(position), position)"
                                else "${Consts.className}.${element.simpleName}(${contextStatement}, ${Consts.dataSet}.get(position), position)"
                            )
                            .addStatement("return true")
                            .build()
                    )
                    .build()
                methodSpecBuilder.addStatement("holder.itemView.findViewById(${onLongClickAnnotation.itemId}).setOnLongClickListener(${listener})")
            }
        }
    }
}