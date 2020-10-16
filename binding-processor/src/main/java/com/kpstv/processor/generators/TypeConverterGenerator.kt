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

        val typeStatement = "${Consts.CLASSNAME_TYPE} type = new ${Consts.CLASSNAME_TYPETOKEN}<${parameter}>(){}.getType()"

        val toMethodCode = CodeBlock.builder()
            .addStatement("if (${Consts.converterName} == null) return null")
            .addStatement(typeStatement)

        val fromMethodCode = CodeBlock.builder()
            .addStatement("if (${Consts.converterName} == null) return null")
            .addStatement(typeStatement)

        when(converterType) {
            ConverterType.GSON -> {
                toMethodCode
                    .addStatement("${Consts.CLASSNAME_GSON} gson = new ${Consts.CLASSNAME_GSON}()")
                    .addStatement("return gson.toJson(${Consts.converterName}, type)")

                fromMethodCode
                    .addStatement("${Consts.CLASSNAME_GSON} gson = new ${Consts.CLASSNAME_GSON}()")
                    .addStatement("return gson.fromJson(${Consts.converterName}, type)")
            }
            ConverterType.MOSHI -> {
                toMethodCode
                    .addStatement("return new ${Consts.ClASSNAME_MOSHI}.Builder().build().adapter(type).toJson(${Consts.converterName})")
                fromMethodCode
                    .add("try {\n")
                    .addStatement("\treturn (${parameter}) new ${Consts.ClASSNAME_MOSHI}.Builder().build().adapter(type).fromJson(${Consts.converterName})")
                    .add("} catch (${IOException::class.qualifiedName} e) {\n")
                    .addStatement("\te.printStackTrace()")
                    .addStatement("\treturn null")
                    .add("}\n")
            }
            ConverterType.KOTLIN_SERIALIZATION -> {
                val serializer = "${Consts.CLASSNAME_KSERIALIZER} serializer = ${Consts.CLASSNAME_SERIALIZERSKT}.serializer(${Consts.CLASSANAME_KX_JSON}.Default.getSerializersModule(), ${Consts.CLASSNAME_KX_REFLECTION}.typeof(${originalClassName}.class))"
                toMethodCode
                    .addStatement(serializer)
                    .addStatement("return ${Consts.CLASSANAME_KX_JSON}.Default.encodeToString(serializer, ${Consts.converterName})")
                fromMethodCode
                    .addStatement(serializer)
                    .addStatement("return ${originalClassName}${Consts.CLASSANAME_KX_JSON}.Default.decodeFromString(serializer, ${Consts.converterName})")
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