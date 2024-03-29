package com.kpstv.processor.generators.auto

import com.kpstv.bindings.ConverterType
import com.kpstv.processor.abstract.BaseAutoGenerator
import com.kpstv.processor.utils.AutoGeneratorDataType
import com.kpstv.processor.utils.AutoGeneratorType
import com.kpstv.processor.utils.Consts
import com.squareup.javapoet.*
import javax.lang.model.element.Modifier

class TypeConverterProcessor(
    override val typeSpecBuilder: TypeSpec.Builder,
    override val serializerType: ConverterType,
    private val firstClassType: ClassName,
    private val enumList: List<TypeName>,
    generatorDataType: AutoGeneratorDataType,
    secondClassType: TypeName? = null,
) : BaseAutoGenerator() {

    override val converterType = AutoGeneratorType.ROOM

    override val baseDataType: TypeName = when(generatorDataType) {
        AutoGeneratorDataType.DATA -> firstClassType
        AutoGeneratorDataType.LIST -> ParameterizedTypeName.get(Consts.CLASSNAME_LIST, firstClassType)
        AutoGeneratorDataType.MAP -> ParameterizedTypeName.get(Consts.CLASSNAME_MAP, secondClassType, firstClassType)
        AutoGeneratorDataType.PAIR -> ParameterizedTypeName.get(Consts.CLASSNAME_PAIR, secondClassType, firstClassType)
    }

    override fun encodeBuilder(): MethodSpec.Builder {
        return MethodSpec.methodBuilder(Consts.toConvertMethod + firstClassType.simpleName())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation(Consts.CLASSNAME_TYPECONVERTER)
            .addAnnotation(Consts.CLASSNAME_NULLABLE)
            .addParameter(baseDataType, Consts.converterName)
            .returns(String::class.java)
    }

    override fun decodeBuilder(): MethodSpec.Builder {
        return MethodSpec.methodBuilder(Consts.fromConvertMethod + firstClassType.simpleName())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation(Consts.CLASSNAME_TYPECONVERTER)
            .addAnnotation(Consts.CLASSNAME_NULLABLE)
            .addParameter(String::class.java, Consts.converterName)
            .returns(baseDataType)
    }

    override fun create() {
        super.create()
        for(enum in enumList) {
            EnumGenericConverter.generate(typeSpecBuilder, enum, firstClassType.packageName())
        }
    }
}