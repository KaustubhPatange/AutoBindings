package com.kpstv.processor.generators

import com.kpstv.bindings.ConverterType
import com.kpstv.processor.utils.Consts
import com.squareup.javapoet.*
import java.io.IOException
import javax.lang.model.element.Modifier

object TypeConverterGenerator {
    fun create(typeSpecBuilder: TypeSpec.Builder, converterType: ConverterType, originalClassName: ClassName, isListConverter: Boolean) {
        val parameterizedTypeName = ParameterizedTypeName.get(Consts.CLASSNAME_LIST, originalClassName)
        val parameter = if (!isListConverter) originalClassName.simpleName() else "List<${originalClassName.simpleName()}>"

        val toMethodCode = CodeBlock.builder()
            .addStatement("if (${Consts.converterName} == null) return null")

        val fromMethodCode = CodeBlock.builder()
            .addStatement("if (${Consts.converterName} == null) return null")

        when(converterType) {
            ConverterType.GSON -> {
                val typeStatement = CodeBlock.builder()
                    .addStatement("\$T type = new \$T<${parameter}>(){}.getType()", Consts.CLASSNAME_TYPE, Consts.CLASSNAME_TYPETOKEN)
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
                /**
                 * For List<T> data classes needs @JsonClass(generateAdapter = true) annotation
                 */
                val typeStatement = if (!isListConverter)
                    CodeBlock.builder().add("\$T.class", originalClassName).build()
                else CodeBlock.builder().add("\$T.newParameterizedType(\$T.class, \$T.class)", Consts.CLASSNAME_MOSHI_TYPES, List::class.java, originalClassName).build()
                toMethodCode
                    .add("return new \$T.Builder().build().adapter(", Consts.ClASSNAME_MOSHI)
                    .add(typeStatement)
                    .add(").toJson(${Consts.converterName});")
                fromMethodCode
                    .add("try {\n")
                    .add("\treturn (${parameter}) new \$T.Builder().build().adapter(", Consts.ClASSNAME_MOSHI)
                    .add(typeStatement)
                    .add(").fromJson(${Consts.converterName});\n")
                    .add("} catch (\$T e) {\n", IOException::class.java)
                    .addStatement("\te.printStackTrace()")
                    .addStatement("\treturn null")
                    .add("}\n")
            }
            ConverterType.KOTLIN_SERIALIZATION -> {
                /**
                 * Class requires @Serializable annotation
                 */
                val reflectionType = if (!isListConverter)
                    CodeBlock.builder().add("\$T.typeOf(\$T.class))", Consts.CLASSNAME_KX_REFLECTION, originalClassName).build()
                else
                    CodeBlock.builder()
                        .add("\$T.typeOf(\$T.class, \$T.Companion.invariant(\$T.typeOf(\$T.class))))",
                            Consts.CLASSNAME_KX_REFLECTION, List::class.java, Consts.CLASSNAME_KTYPE_PROJECTION, Consts.CLASSNAME_KX_REFLECTION, originalClassName)
                        .build()

                val serializer = CodeBlock.builder()
                    .add("\$T serializer = \$T.serializer(\$T.Default.getSerializersModule(), ",
                        Consts.CLASSNAME_KSERIALIZER, Consts.CLASSNAME_SERIALIZERSKT, Consts.CLASSANAME_KX_JSON,)
                    .add(reflectionType)
                    .build()

                toMethodCode
                    .addStatement(serializer)
                    .addStatement("return \$T.Default.encodeToString(serializer, ${Consts.converterName})", Consts.CLASSANAME_KX_JSON)
                fromMethodCode
                    .addStatement(serializer)
                    .addStatement("return (${parameter})\$T.Default.decodeFromString(serializer, ${Consts.converterName})", Consts.CLASSANAME_KX_JSON)
            }
        }

        val toMethodGenerator = MethodSpec.methodBuilder(Consts.toConvertMethod + originalClassName.simpleName())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation(Consts.CLASSNAME_TYPECONVERTER)
            .returns(String::class.java)
            .addCode(toMethodCode.build())

        val fromMethodGenerator = MethodSpec.methodBuilder(Consts.fromConvertMethod + originalClassName.simpleName())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation(Consts.CLASSNAME_TYPECONVERTER)
            .addParameter(String::class.java, Consts.converterName)
            .addCode(fromMethodCode.build())

        if (!isListConverter) {
            toMethodGenerator.addParameter(originalClassName, Consts.converterName)
            fromMethodGenerator.returns(originalClassName)
        }
        else {
            toMethodGenerator.addParameter(parameterizedTypeName, Consts.converterName)
            fromMethodGenerator.returns(parameterizedTypeName)
        }

        typeSpecBuilder.addMethod(toMethodGenerator.build())
        typeSpecBuilder.addMethod(fromMethodGenerator.build())
    }
}