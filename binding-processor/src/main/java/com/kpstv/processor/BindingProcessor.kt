package com.kpstv.processor

import com.kpstv.bindings.*
import com.kpstv.processor.generators.*
import com.kpstv.processor.utils.Consts
import com.kpstv.processor.utils.Utils
import com.kpstv.processor.utils.getAnnotationClassValue
import com.squareup.javapoet.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter


@SupportedAnnotationTypes(
    "com.kpstv.bindings.AutoGenerateConverter",
    "com.kpstv.bindings.AutoGenerateListConverter",
    "com.kpstv.bindings.RecyclerViewAdapter",
    "com.kpstv.bindings.RecyclerViewListAdapter"
)
class BindingProcessor : AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        env: RoundEnvironment?
    ): Boolean {
        parseRecyclerViewAnnotation(env)
        parseRecyclerViewListAnnotation(env)
        parseAutoGenerateAnnotations(env)
        return true
    }

    private fun parseAutoGenerateAnnotations(env: RoundEnvironment?) {
        val autoGenerateAnnotationTypes =
            env?.getElementsAnnotatedWith(AutoGenerateConverter::class.java)
        val normalTypes = ElementFilter.typesIn(autoGenerateAnnotationTypes).toMutableList()

        val autoGenerateListAnnotationTypes =
            env?.getElementsAnnotatedWith(AutoGenerateListConverter::class.java)
        val listTypes = ElementFilter.typesIn(autoGenerateListAnnotationTypes).toMutableList()

        normalTypes.forEach { typeElement ->
            val annotationAttributes = typeElement.getAnnotation(AutoGenerateConverter::class.java)
            commonParseConverterAnnotation(typeElement, annotationAttributes.using, false)
        }
        listTypes.forEach { typeElement ->
            val annotationAttributes = typeElement.getAnnotation(AutoGenerateListConverter::class.java)
            commonParseConverterAnnotation(typeElement, annotationAttributes.using, true)
        }
    }

    private fun commonParseConverterAnnotation(typeElement: TypeElement, using: ConverterType, isListConverter: Boolean) {
        val packageName =
            processingEnv.elementUtils.getPackageOf(typeElement).qualifiedName.toString()

        val typeName = typeElement.simpleName.toString()

        val originalClassName = ClassName.get(packageName, typeName)

        val generatedClassName = ClassName.get(packageName, typeName + if (!isListConverter) Consts.converterSuffix else Consts.converterListSuffix)

        val converterBuilder = TypeSpec.classBuilder(generatedClassName)
            .addModifiers(Modifier.PUBLIC)
            .also { TypeConverterGenerator.create(it, using, originalClassName, isListConverter) }

        Utils.write(packageName, converterBuilder.build(), typeElement, processingEnv)
    }

    private fun parseRecyclerViewListAnnotation(env: RoundEnvironment?) {
        val recyclerViewListAnnotations =
            env?.getElementsAnnotatedWith(RecyclerViewListAdapter::class.java)
        val types = ElementFilter.typesIn(recyclerViewListAnnotations).toMutableList()

        types.forEach { typeElement ->
            val dataTypeMirror =
                typeElement.getAnnotationClassValue<RecyclerViewListAdapter> { dataSetType }

            val packageName =
                processingEnv.elementUtils.getPackageOf(typeElement).qualifiedName.toString()
            val typeName = typeElement.simpleName.toString()

            val originalClassName = ClassName.get(packageName, typeName)
            val generatedClassName = ClassName.get(packageName, Consts.adapterPrefix + typeName)

            val viewHolderClassName =
                ClassName.get(generatedClassName.canonicalName(), typeName + Consts.holderSuffix)

            val extendedAdapterClassName = ParameterizedTypeName.get(
                Consts.CLASSNAME_LISTADAPTER,
                TypeName.get(dataTypeMirror),
                TypeVariableName.get(viewHolderClassName.canonicalName()).box()
            )

            val diffUtil = DiffUtilGenerator.generateDiffUtil(
                typeElement,
                TypeName.get(dataTypeMirror)
            )

            val adapterBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addField(originalClassName, Consts.className, Modifier.PRIVATE)
                .addType(ViewHolderGenerator.createViewHolder(viewHolderClassName))
                .superclass(extendedAdapterClassName)
                .addMethod(
                    MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(originalClassName, Consts.className, Modifier.FINAL)
                        .addStatement("super(${diffUtil})")
                        .addStatement("this.${Consts.className} = ${Consts.className}")
                        .build()
                )
                .also { BindViewGenerator.generateOnBindViewListHolder(it, typeElement, viewHolderClassName) }
                .addMethod(ViewTypeGenerator.generateItemViewType(typeElement))

            Utils.write(packageName, adapterBuilder.build(), typeElement, processingEnv)
        }
    }

    private fun parseRecyclerViewAnnotation(env: RoundEnvironment?) {
        val recyclerViewAnnotations = env?.getElementsAnnotatedWith(RecyclerViewAdapter::class.java)
        val types = ElementFilter.typesIn(recyclerViewAnnotations).toMutableList()
        types.forEach { typeElement ->
            val dataTypeMirror =
                typeElement.getAnnotationClassValue<RecyclerViewAdapter> { dataSetType }

            val packageName =
                processingEnv.elementUtils.getPackageOf(typeElement).qualifiedName.toString()
            val typeName = typeElement.simpleName.toString()

            val originalClassName = ClassName.get(packageName, typeName)
            val generatedClassName = ClassName.get(packageName, Consts.adapterPrefix + typeName)

            val viewHolderClassName =
                ClassName.get(generatedClassName.canonicalName(), typeName + Consts.holderSuffix)

            // Start building adapter
            val extendedAdapterClassName = ClassName.get(
                Consts.CLASSNAME_RECYCLERVIEW.packageName(),
                Consts.CLASSNAME_RECYCLERVIEW.simpleName(),
                "Adapter<${generatedClassName}.${viewHolderClassName.simpleName()}>"
            )

            val dataSetParameter = ParameterizedTypeName.get(
                ClassName.get(MutableList::class.java),
                TypeName.get(dataTypeMirror)
            )
            val adapterBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addType(ViewHolderGenerator.createViewHolder(viewHolderClassName))
                .superclass(extendedAdapterClassName)
                .addField(
                    dataSetParameter, Consts.dataSet, Modifier.PRIVATE
                )
                .addField(originalClassName, Consts.className, Modifier.PRIVATE)
                .addMethod(
                    MethodSpec.constructorBuilder()
                        .addParameter(originalClassName, Consts.className)
                        .addParameter(dataSetParameter, Consts.dataSet)
                        .addStatement("this.${Consts.className} = ${Consts.className}")
                        .addStatement("this.${Consts.dataSet} = ${Consts.dataSet}")
                        .addModifiers(Modifier.PUBLIC)
                        .build()
                )
                .also { BindViewGenerator.generateOnBindViewHolder(it, typeElement, viewHolderClassName) }
                .addMethod(ViewTypeGenerator.generateItemViewType(typeElement))
                .addMethod(ItemCountGenerator.generateGetItemCountMethod())

            Utils.write(packageName, adapterBuilder.build(), typeElement, processingEnv)
        }
    }
}