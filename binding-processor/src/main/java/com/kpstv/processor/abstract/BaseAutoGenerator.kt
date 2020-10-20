package com.kpstv.processor.abstract

import com.kpstv.bindings.ConverterType
import com.kpstv.processor.utils.AutoGeneratorDataType
import com.kpstv.processor.utils.AutoGeneratorType
import com.kpstv.processor.utils.Consts
import com.squareup.javapoet.*
import java.io.IOException

abstract class BaseAutoGenerator {

    protected abstract val converterType: AutoGeneratorType

    protected abstract val serializerType: ConverterType

    protected abstract val baseDataType: TypeName

    protected abstract val generatorDataType: AutoGeneratorDataType

    protected abstract val typeSpecBuilder: TypeSpec.Builder

    protected abstract fun encodeBuilder(): MethodSpec.Builder

    protected abstract fun decodeBuilder(): MethodSpec.Builder

    fun create() {
        val toMethodCode = CodeBlock.builder()
            .addStatement("if (${Consts.converterName} == null) return null")

        val fromMethodCode = CodeBlock.builder()
            .addStatement("if (${Consts.converterName} == null) return null")

        when(serializerType) {
            ConverterType.GSON -> {
                val typeStatement = CodeBlock.builder()
                    .addStatement("\$T type = new \$T<\$T>(){}.getType()", Consts.CLASSNAME_TYPE, Consts.CLASSNAME_TYPETOKEN, baseDataType)
                    .addStatement("\$T gson = new \$T()", Consts.CLASSNAME_GSON, Consts.CLASSNAME_GSON)
                    .build()
                toMethodCode
                    .add(typeStatement)
                    .addStatement("return gson.toJson(${Consts.converterName}, type)")

                fromMethodCode
                    .add(typeStatement)
                    .addStatement("return gson.fromJson(${Consts.converterName}, type)")
            }
            ConverterType.MOSHI -> {
                val typeStatement =
                    if (baseDataType is ParameterizedTypeName) {
                        val parameterizedTypeName = (baseDataType as ParameterizedTypeName)
                        val size = parameterizedTypeName.typeArguments.size
                        val builder = CodeBlock.builder().add("\$T.newParameterizedType(\$T.class,", Consts.CLASSNAME_MOSHI_TYPES, parameterizedTypeName.rawType)
                        for ((index, type) in parameterizedTypeName.typeArguments.withIndex()) {
                            var text = "\$T.class"
                            if (index != size -1)
                                text += ","
                            builder.add(text, type)
                        }
                        builder.add(")").build()
                    } else
                        CodeBlock.builder().add("\$T.class", baseDataType).build()

                toMethodCode
                    .add("return new \$T.Builder().build().adapter(", Consts.ClASSNAME_MOSHI)
                    .add(typeStatement)
                    .add(").toJson(${Consts.converterName});")
                fromMethodCode
                    .add("try {\n")
                    .add("\treturn (\$T) new \$T.Builder().build().adapter(", baseDataType, Consts.ClASSNAME_MOSHI)
                    .add(typeStatement)
                    .add(").fromJson(${Consts.converterName});\n")
                    .add("} catch (\$T e) {\n", IOException::class.java)
                    .addStatement("\te.printStackTrace()")
                    .addStatement("\treturn null")
                    .add("}\n")
            }
            ConverterType.KOTLIN_SERIALIZATION -> {
                val typeStatement =
                    if (baseDataType is ParameterizedTypeName) {
                        val parameterizedTypeName = (baseDataType as ParameterizedTypeName)
                        val size = parameterizedTypeName.typeArguments.size
                        val builder = CodeBlock.builder().add("\$T.typeOf(\$T.class,", Consts.CLASSNAME_KX_REFLECTION, parameterizedTypeName.rawType)
                        for ((index, type) in parameterizedTypeName.typeArguments.withIndex()) {
                            var text = "\$T.Companion.invariant(\$T.typeOf(\$T.class))"
                            if (index != size -1)
                                text += ","
                            builder.add(text, Consts.CLASSNAME_KTYPE_PROJECTION, Consts.CLASSNAME_KX_REFLECTION, type)
                        }
                        builder.add("))").build()
                    } else
                        CodeBlock.builder().add("\$T.typeOf(\$T.class))", Consts.CLASSNAME_KX_REFLECTION, baseDataType).build()

                val serializer = CodeBlock.builder()
                    .add("\$T serializer = \$T.serializer(\$T.Default.getSerializersModule(), ",
                        Consts.CLASSNAME_KSERIALIZER, Consts.CLASSNAME_SERIALIZERSKT, Consts.CLASSANAME_KX_JSON,)
                    .add(typeStatement)
                    .build()

                toMethodCode
                    .addStatement(serializer)
                    .addStatement("return \$T.Default.encodeToString(serializer, ${Consts.converterName})", Consts.CLASSANAME_KX_JSON)
                fromMethodCode
                    .addStatement(serializer)
                    .addStatement("return (\$T)\$T.Default.decodeFromString(serializer, ${Consts.converterName})", baseDataType, Consts.CLASSANAME_KX_JSON)
            }
        }

        typeSpecBuilder.addMethod(encodeBuilder().addCode(toMethodCode.build()).build())
        typeSpecBuilder.addMethod(decodeBuilder().addCode(fromMethodCode.build()).build())
    }
}