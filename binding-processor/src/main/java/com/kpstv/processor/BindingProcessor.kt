package com.kpstv.processor

import com.kpstv.bindings.*
import com.kpstv.processor.generators.*
import com.kpstv.processor.generators.auto.SQLDelightAdapterProcessor
import com.kpstv.processor.generators.auto.TypeConverterProcessor
import com.kpstv.processor.utils.*
import com.squareup.javapoet.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic

class BindingProcessor : AbstractProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            AutoGenerateSQLDelightAdapters::class.java.canonicalName,
            AutoGenerateConverter::class.java.canonicalName,
            AutoGenerateMapConverter::class.java.canonicalName,
            AutoGeneratePairConverter::class.java.canonicalName,
            AutoGenerateListConverter::class.java.canonicalName,
            RecyclerViewListAdapter::class.java.canonicalName,
            RecyclerViewAdapter::class.java.canonicalName,
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        env: RoundEnvironment?,
    ): Boolean {
        parseRecyclerViewAnnotation(env)
        parseRecyclerViewListAnnotation(env)
        parseAutoGenerateAnnotations(env)
        parseAutoGenerateSQLDelightAnnotations(env)
        return true
    }

    private fun parseAutoGenerateSQLDelightAnnotations(env: RoundEnvironment?) {
        val autoGenerateSQLDelightAnnotationTypes =
            env?.getElementsAnnotatedWith(AutoGenerateSQLDelightAdapters::class.java)
        val elements = ElementFilter.typesIn(autoGenerateSQLDelightAnnotationTypes).toMutableList()
        elements.forEach { typeElement ->

            if (!typeElement.modifiers.contains(Modifier.ABSTRACT))
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "${typeElement.qualifiedName}: Class must be an interface")

            val packageName = processingEnv.elementUtils.getPackageOf(typeElement).qualifiedName.toString()
            val originalClassName = ClassName.get(packageName, typeElement.simpleName.toString())
            val generatedClassName = ClassName.get(packageName, typeElement.simpleName.toString() + "Impl")

            val adapterBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(originalClassName)
                .addField(
                    FieldSpec.builder(generatedClassName, "Instance", Modifier.PRIVATE, Modifier.STATIC)
                        .initializer("new \$T()", generatedClassName).build()
                )

            val serializerType = typeElement.getAnnotation(AutoGenerateSQLDelightAdapters::class.java).using

            typeElement.enclosedElements.forEach enclosed@{ enclosedElement ->
                val executableElement = enclosedElement as ExecutableElement
                val returnType = ParameterizedTypeName.get(executableElement.returnType) as ParameterizedTypeName

                val baseType = returnType.typeArguments[0]
                val toType = returnType.typeArguments[1]
                if (toType.simpleName() != "String") {
                    processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "${executableElement.simpleName}: Method's return type must be ColumnAdapter<*, String>")
                    return@enclosed
                }

                adapterBuilder.addMethod(
                    SQLDelightAdapterProcessor(
                        adapterName = enclosedElement.simpleName.toString(),
                        serializerType = serializerType,
                        baseDataType = baseType
                    ).generateMethod()
                )
                adapterBuilder.addField(
                    FieldSpec.builder(returnType, enclosedElement.simpleName.toString(), Modifier.PUBLIC, Modifier.STATIC)
                        .initializer("Instance.${enclosedElement.simpleName}()")
                        .build()
                )
            }

            Utils.write(packageName, adapterBuilder.build(), processingEnv)
        }
    }

    private fun parseAutoGenerateAnnotations(env: RoundEnvironment?) {
        val autoGenerateAnnotationTypes =
            env?.getElementsAnnotatedWith(AutoGenerateConverter::class.java)
        val normalTypes = ElementFilter.typesIn(autoGenerateAnnotationTypes).toMutableList()

        val autoGenerateListAnnotationTypes =
            env?.getElementsAnnotatedWith(AutoGenerateListConverter::class.java)
        val listTypes = ElementFilter.typesIn(autoGenerateListAnnotationTypes).toMutableList()

        val autoGenerateMapAnnotationTypes =
            env?.getElementsAnnotatedWith(AutoGenerateMapConverter::class.java)
        val mapTypes = ElementFilter.typesIn(autoGenerateMapAnnotationTypes).toMutableList()

        val autoGeneratePairAnnotationTypes =
            env?.getElementsAnnotatedWith(AutoGeneratePairConverter::class.java)
        val pairTypes = ElementFilter.typesIn(autoGeneratePairAnnotationTypes).toMutableList()

        normalTypes.forEach { typeElement ->
            val annotationAttributes = typeElement.getAnnotation(AutoGenerateConverter::class.java)
            commonParseConverterAnnotation(typeElement,
                annotationAttributes.using,
                AutoGeneratorDataType.DATA)
        }
        listTypes.forEach { typeElement ->
            val annotationAttributes =
                typeElement.getAnnotation(AutoGenerateListConverter::class.java)
            commonParseConverterAnnotation(typeElement,
                annotationAttributes.using,
                AutoGeneratorDataType.LIST)
        }
        mapTypes.forEach { typeElement ->
            val annotationAttributes =
                typeElement.getAnnotation(AutoGenerateMapConverter::class.java)
            val mapValue =
                typeElement.getAnnotationClassValue<AutoGenerateMapConverter> { annotationAttributes.keyClass }
            commonParseConverterAnnotation(typeElement,
                annotationAttributes.using,
                AutoGeneratorDataType.MAP,
                TypeName.get(mapValue))
        }
        pairTypes.forEach { typeElement ->
            val annotationAttributes =
                typeElement.getAnnotation(AutoGeneratePairConverter::class.java)
            val mapValue =
                typeElement.getAnnotationClassValue<AutoGeneratePairConverter> { annotationAttributes.keyClass }
            commonParseConverterAnnotation(typeElement,
                annotationAttributes.using,
                AutoGeneratorDataType.PAIR,
                TypeName.get(mapValue))
        }
    }

    private fun commonParseConverterAnnotation(
        typeElement: TypeElement,
        using: ConverterType,
        generatorDataType: AutoGeneratorDataType,
        secondClass: TypeName? = null,
    ) {
        val packageName =
            processingEnv.elementUtils.getPackageOf(typeElement).qualifiedName.toString()

        val typeName = typeElement.simpleName.toString()
        val originalClassName = ClassName.get(packageName, typeName)

        val generatedClassName =
            ClassName.get(packageName, typeName + Utils.getAppropriateSuffix(generatorDataType))

        val converterBuilder = TypeSpec.classBuilder(generatedClassName)
            .addModifiers(Modifier.PUBLIC)
            .also {
                TypeConverterProcessor(
                    typeSpecBuilder = it,
                    serializerType = using,
                    generatorDataType = generatorDataType,
                    firstClassType = originalClassName,
                    secondClassType = secondClass
                ).create()
            }

        Utils.write(packageName, converterBuilder.build(), processingEnv, typeElement)
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
                .also {
                    BindViewGenerator.generateOnBindViewListHolder(it,
                        typeElement,
                        viewHolderClassName)
                }
                .addMethod(ViewTypeGenerator.generateItemViewType(typeElement))

            Utils.write(packageName, adapterBuilder.build(), processingEnv, typeElement)
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
                .also {
                    BindViewGenerator.generateOnBindViewHolder(it,
                        typeElement,
                        viewHolderClassName)
                }
                .addMethod(ViewTypeGenerator.generateItemViewType(typeElement))
                .addMethod(ItemCountGenerator.generateGetItemCountMethod())

            Utils.write(packageName, adapterBuilder.build(), processingEnv, typeElement)
        }
    }
}