package com.kpstv.processor

import com.kpstv.library_annotations.Bind
import com.kpstv.library_annotations.RecyclerViewAdapter
import com.kpstv.processor.generators.BindViewGenerator
import com.kpstv.processor.generators.ItemCountGenerator
import com.kpstv.processor.generators.ViewHolderGenerator
import com.kpstv.processor.utils.Consts
import com.kpstv.processor.utils.Utils
import com.kpstv.processor.utils.getAnnotationClassValue
import com.kpstv.processor.utils.getElementFromAnnotation
import com.squareup.javapoet.*
import java.io.IOException
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic


@SupportedAnnotationTypes(
    "com.kpstv.library_annotations.RecyclerViewAdapter",
    "com.kpstv.library_annotations.Bind",
    "com.kpstv.library_annotations.OnClick",
    "com.kpstv.library_annotations.OnLongClick"
)
class BindingProcessor : AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        env: RoundEnvironment?
    ): Boolean {

        val recyclerViewAnnotations = env?.getElementsAnnotatedWith(RecyclerViewAdapter::class.java)
        val types = ElementFilter.typesIn(recyclerViewAnnotations).toMutableList()
        types.forEach { typeElement ->
            val annotation = typeElement.getAnnotation(RecyclerViewAdapter::class.java)
            val layoutId = annotation.layoutId
            val dataTypeMirror =
                typeElement.getAnnotationClassValue<RecyclerViewAdapter> { dataSetType }

            val packageName =
                processingEnv.elementUtils.getPackageOf(typeElement).qualifiedName.toString()
            val typeName = typeElement.simpleName.toString()

            val originalClassName = ClassName.get(packageName, typeName)
            val generatedClassName = ClassName.get(packageName, Consts.adapterPrefix + typeName)

            val viewHolderClassName = ClassName.get(generatedClassName.canonicalName(), typeName + Consts.holderSuffix)

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
                .addMethod(
                    ViewHolderGenerator.generateOnCreateViewHolder(
                        viewHolderClassName, layoutId
                    )
                )
                .addMethod(BindViewGenerator.generateOnBindViewHolder(typeElement, viewHolderClassName))
                .addMethod(ItemCountGenerator.generateGetItemCountMethod())

            Utils.write(packageName, adapterBuilder.build(), typeElement, processingEnv)
        }
        return true
    }
}