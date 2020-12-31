package com.kpstv.processor.generators.auto

import com.kpstv.processor.utils.Consts
import com.kpstv.processor.utils.simpleName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

object EnumGenericConverter {
    fun generate(typeSpecBuilder: TypeSpec.Builder, typeName: TypeName) {
        val encodeMethodBuilder = MethodSpec.methodBuilder(Consts.toConvertMethod + typeName.simpleName())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation(Consts.CLASSNAME_TYPECONVERTER)
            .addParameter(typeName, Consts.converterName)
            .returns(String::class.java)

        encodeMethodBuilder.addCode("return ${Consts.converterName}.name();")

        val decodeMethodBuilder = MethodSpec.methodBuilder(Consts.fromConvertMethod + typeName.simpleName())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation(Consts.CLASSNAME_TYPECONVERTER)
            .addParameter(String::class.java, Consts.converterName)
            .returns(typeName)

        decodeMethodBuilder.addCode("return \$T.valueOf(${Consts.converterName});", typeName)

        typeSpecBuilder.addMethod(encodeMethodBuilder.build())
        typeSpecBuilder.addMethod(decodeMethodBuilder.build())
    }
}