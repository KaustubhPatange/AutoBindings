package com.kpstv.processor.generators

import com.kpstv.library_annotations.GlideLoadArray
import com.kpstv.library_annotations.OnBind
import com.kpstv.processor.utils.BINDTYPE
import com.kpstv.processor.utils.ClickUtils
import com.kpstv.processor.utils.Consts
import com.kpstv.processor.utils.Utils
import com.squareup.javapoet.*
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

object BindViewGenerator {

    fun generateOnBindViewHolder(
        typeSpecBuilder: TypeSpec.Builder,
        typeElement: TypeElement,
        viewHolderClassName: ClassName
    ) {
        generateOnBindViewHolder(typeSpecBuilder, typeElement, viewHolderClassName, false)
    }

    fun generateOnBindViewListHolder(
        typeSpecBuilder: TypeSpec.Builder,
        typeElement: TypeElement,
        viewHolderClassName: ClassName
    ) {
        generateOnBindViewHolder(typeSpecBuilder, typeElement, viewHolderClassName, true)
    }

    private fun generateOnBindViewHolder(
        typeSpecBuilder: TypeSpec.Builder,
        typeElement: TypeElement,
        viewHolderClassName: ClassName,
        isListAdapter: Boolean
    ) {
        val holderCodeGenerator = CodeBlock.builder()
        val bindCodeBuilder = CodeBlock.builder()

        typeElement.enclosedElements?.forEach { bindElement ->
            val bindAnnotation = bindElement.getAnnotation(OnBind::class.java)
            if (bindAnnotation != null) {
                val holderBuilder = StringBuilder().apply {
                    append("if (viewType == ${bindAnnotation.viewType}) {\n")
                    append("\tfinal ${viewHolderClassName.simpleName()} holder = new ${viewHolderClassName.simpleName()}(android.view.LayoutInflater.from(parent.getContext()).inflate(${bindAnnotation.layoutId}, parent, false));\n")
                    append(
                        ClickUtils.generateClickListener(
                            typeElement,
                            BINDTYPE.VIEWHOLDER,
                            isListAdapter,
                            bindAnnotation.viewType
                        )
                    )
                    append("\treturn holder;\n")
                    append("}\n")
                }
                holderCodeGenerator.add(holderBuilder.toString())

                val bindBuilder = StringBuilder().apply {
                    append("if (getItemViewType(${Consts.position}) == ${bindAnnotation.viewType}) {\n")
                    if (isListAdapter)
                        append("\t${Consts.className}.${bindElement?.simpleName}(${Consts.holder}.itemView, getItem(${Consts.position}), ${Consts.position});\n")
                    else append("\t${Consts.className}.${bindElement?.simpleName}(${Consts.holder}.itemView, ${Consts.dataSet}.get(${Consts.position}), ${Consts.position});\n")
                    append("\t${generateBindAnnotations(bindElement, isListAdapter)}")
                    append(
                        ClickUtils.generateClickListener(
                            typeElement,
                            BINDTYPE.BIND,
                            isListAdapter,
                            bindAnnotation.viewType
                        )
                    )
                    append("}\n")
                }

                bindCodeBuilder.add(bindBuilder.toString())
            }
        }

        typeSpecBuilder.addMethod(
            ViewHolderGenerator.generateOnCreateViewHolder(
                viewHolderClassName,
                holderCodeGenerator.build()
            )
        )
        typeSpecBuilder.addMethod(
            generateOnBindHolder(
                viewHolderClassName,
                bindCodeBuilder.build()
            )
        )
    }

    private fun generateOnBindHolder(
        viewHolderClassName: ClassName,
        bindCodeBlock: CodeBlock
    ): MethodSpec {
        return MethodSpec.methodBuilder("onBindViewHolder")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addParameter(viewHolderClassName, Consts.holder, Modifier.FINAL)
            .addParameter(TypeName.INT, Consts.position, Modifier.FINAL)
            .addCode(bindCodeBlock)
            /*.addCode(
                ClickUtils.generateClickListener(
                    typeElement,
                    BINDTYPE.BIND,
                    isListAdapter,
                    viewType
                )
            )*/
            .build()
    }

    private fun generateBindAnnotations(
        bindElement: Element?,
        isListAdapter: Boolean = false
    ): CodeBlock {
        val codeBlockBuilder = CodeBlock.builder()
        val getItemStatement = if (!isListAdapter) "${Consts.dataSet}.get(${Consts.position})"
        else "getItem(${Consts.position})"

        val glideLoadArrayAnnotation = bindElement?.getAnnotation(GlideLoadArray::class.java)
        glideLoadArrayAnnotation?.glideLoad?.forEach {
            val glideBlock = StringBuilder().apply {
                append("com.bumptech.glide.Glide.with(${Consts.holder}.itemView.getContext())")
                append(".load(${getItemStatement}.${Utils.prepareJavaModelName(it.data)})")
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
            codeBlockBuilder.apply {
                addStatement(glideBlock.toString())
            }
        }
        return codeBlockBuilder.build()
    }
}