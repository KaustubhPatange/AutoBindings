package com.kpstv.processor.generators.auto

import androidx.annotation.NonNull
import com.kpstv.bindings.ConverterType
import com.kpstv.processor.abstract.BaseAutoGenerator
import com.kpstv.processor.utils.AutoGeneratorDataType
import com.kpstv.processor.utils.AutoGeneratorType
import com.kpstv.processor.utils.Consts
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier

class SQLDelightAdapterProcessor(
    override val serializerType: ConverterType,
    override val generatorDataType: AutoGeneratorDataType,
    private val adapterName: String,
    firstClassType: TypeName,
    secondClassType: TypeName? = null,
) : BaseAutoGenerator() {

    override val converterType = AutoGeneratorType.SQLDelight

    override val baseDataType: TypeName = when (generatorDataType) {
        AutoGeneratorDataType.DATA -> firstClassType
        AutoGeneratorDataType.LIST -> ParameterizedTypeName.get(Consts.CLASSNAME_LIST, firstClassType)
        AutoGeneratorDataType.MAP -> ParameterizedTypeName.get(Consts.CLASSNAME_MAP, secondClassType, firstClassType)
        AutoGeneratorDataType.PAIR -> ParameterizedTypeName.get(Consts.CLASSNAME_PAIR, secondClassType, firstClassType)
    }

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

    fun generateField(): FieldSpec {
        create()
        return FieldSpec.builder(parameterizedType, adapterName, Modifier.PUBLIC, Modifier.STATIC)
            .initializer("\$L", typeSpecBuilder.build())
            .build()
    }
}