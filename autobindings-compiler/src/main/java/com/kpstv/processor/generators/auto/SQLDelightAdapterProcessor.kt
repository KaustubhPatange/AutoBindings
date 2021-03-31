package com.kpstv.processor.generators.auto

import androidx.annotation.NonNull
import com.kpstv.bindings.ConverterType
import com.kpstv.processor.abstract.BaseAutoGenerator
import com.kpstv.processor.utils.AutoGeneratorType
import com.kpstv.processor.utils.Consts
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier

class SQLDelightAdapterProcessor(
    override val serializerType: ConverterType,
    private val adapterName: String,
    override val baseDataType: TypeName,
) : BaseAutoGenerator() {

    override val converterType = AutoGeneratorType.SQLDelight

    private val parameterizedType =
        ParameterizedTypeName.get(Consts.CLASSNAME_COLUMNADAPTER, baseDataType, ClassName.get(String::class.java))

    override val typeSpecBuilder: TypeSpec.Builder = TypeSpec.anonymousClassBuilder("")
        .addSuperinterface(parameterizedType)

    override fun encodeBuilder(): MethodSpec.Builder {
        return MethodSpec.methodBuilder(Consts.encodeMethod)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addAnnotation(NonNull::class.java)
            .addParameter(ParameterSpec.builder(baseDataType, Consts.converterName)
                .addAnnotation(NonNull::class.java).build())
            .returns(String::class.java)
    }

    override fun decodeBuilder(): MethodSpec.Builder {
        return MethodSpec.methodBuilder(Consts.decodeMethod)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addParameter(String::class.java, Consts.converterName)
            .returns(baseDataType)
    }

    fun generateMethod(): MethodSpec {
        create()
        return MethodSpec.methodBuilder(adapterName)
            .addModifiers(Modifier.PUBLIC)
            .returns(parameterizedType)
            .addAnnotation(Override::class.java)
            .addAnnotation(NonNull::class.java)
            .addCode("return \$L;", typeSpecBuilder.build())
            .build()
    }
}