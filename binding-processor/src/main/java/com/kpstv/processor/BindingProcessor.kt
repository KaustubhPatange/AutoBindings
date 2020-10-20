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
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter

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

    override fun process(annotations: MutableSet<out TypeElement>?, env: RoundEnvironment?): Boolean {
        parseRecyclerViewAnnotation(env)
        parseRecyclerViewListAnnotation(env)
        parseAutoGenerateAnnotations(env)
        parseAutoGenerateSQLDelightAnnotations(env)
        return true
    }

    private fun parseAutoGenerateSQLDelightAnnotations(env: RoundEnvironment?) {
        val autoGenerateSQLDelightAnnotationTypes =
            env?.getElementsAnnotatedWith(AutoGenerateSQLDelightAdapters::class.java)
        val elements= ElementFilter.typesIn(autoGenerateSQLDelightAnnotationTypes).toMutableList()
        elements.forEach { typeElement ->
            val adapterBuilder = TypeSpec.classBuilder(Consts.GENERATED_SQLDELIGHTADAPTER)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            val annotationAttributes = typeElement.getAnnotation(AutoGenerateSQLDelightAdapters::class.java)
            annotationAttributes.adapters.forEach { adapterAnnotation ->
                val mainClass =
                    typeElement.getAnnotationClassValue<AutoGenerateSQLDelightAdapters> { adapterAnnotation.source }?.toType()!!
                commonProcessSQLDelightAdapters(adapterBuilder, adapterAnnotation.name, mainClass, annotationAttributes.using, AutoGeneratorDataType.DATA)
            }
            annotationAttributes.listAdapters.forEach { adapterAnnotation ->
                val mainClass =
                    typeElement.getAnnotationClassValue<AutoGenerateSQLDelightAdapters> { adapterAnnotation.source }?.toType()!!
                commonProcessSQLDelightAdapters(adapterBuilder, adapterAnnotation.name, mainClass, annotationAttributes.using, AutoGeneratorDataType.LIST)
            }
            annotationAttributes.mapAdapters.forEach { adapterAnnotation ->
                val firstSource =
                    typeElement.getAnnotationClassValue<AutoGenerateSQLDelightAdapters> { adapterAnnotation.keySource }?.toType()!!
                val secondSource =
                    typeElement.getAnnotationClassValue<AutoGenerateSQLDelightAdapters> { adapterAnnotation.valueSource }?.toType()!!
                commonProcessSQLDelightAdapters(adapterBuilder, adapterAnnotation.name, secondSource, annotationAttributes.using, AutoGeneratorDataType.MAP, firstSource)
            }
            annotationAttributes.pairAdapters.forEach { adapterAnnotation ->
                val firstSource =
                    typeElement.getAnnotationClassValue<AutoGenerateSQLDelightAdapters> { adapterAnnotation.keySource }?.toType()!!
                val secondSource =
                    typeElement.getAnnotationClassValue<AutoGenerateSQLDelightAdapters> { adapterAnnotation.valueSource }?.toType()!!
                commonProcessSQLDelightAdapters(adapterBuilder, adapterAnnotation.name, secondSource, annotationAttributes.using, AutoGeneratorDataType.PAIR, firstSource)
            }

            Utils.write(Consts.GENERATED_SQLDELIGHTADAPTER.packageName(), adapterBuilder.build(), processingEnv)
        }
    }

    private fun commonProcessSQLDelightAdapters(adapterBuilder: TypeSpec.Builder, adapterName: String, firstClass: TypeName,
                                                serializerType: ConverterType, generatorDataType: AutoGeneratorDataType, secondClass: TypeName? = null) {
        val fieldSpec = SQLDelightAdapterProcessor(
            adapterName = adapterName + firstClass.simpleName() + (secondClass?.simpleName() ?: "") + Utils.getAppropriateDelightSuffix(generatorDataType),
            generatorDataType = generatorDataType,
            serializerType = serializerType,
            firstClassType = firstClass,
            secondClassType = secondClass
        ).generateField()
        adapterBuilder.addField(fieldSpec)
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
            commonParseConverterAnnotation(typeElement, annotationAttributes.using, AutoGeneratorDataType.DATA)
        }
        listTypes.forEach { typeElement ->
            val annotationAttributes =
                typeElement.getAnnotation(AutoGenerateListConverter::class.java)
            commonParseConverterAnnotation(typeElement, annotationAttributes.using, AutoGeneratorDataType.LIST)
        }
        mapTypes.forEach { typeElement ->
            val annotationAttributes =
                typeElement.getAnnotation(AutoGenerateMapConverter::class.java)
            val mapValue =
                typeElement.getAnnotationClassValue<AutoGenerateMapConverter> { annotationAttributes.keyClass }
            commonParseConverterAnnotation(typeElement, annotationAttributes.using, AutoGeneratorDataType.MAP, TypeName.get(mapValue))
        }
        pairTypes.forEach { typeElement ->
            val annotationAttributes =
                typeElement.getAnnotation(AutoGeneratePairConverter::class.java)
            val mapValue =
                typeElement.getAnnotationClassValue<AutoGeneratePairConverter> { annotationAttributes.keyClass }
            commonParseConverterAnnotation(typeElement, annotationAttributes.using, AutoGeneratorDataType.PAIR, TypeName.get(mapValue))
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
                TypeConverterProcessor(it,
                    using,
                    generatorDataType,
                    originalClassName,
                    secondClass).create()
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