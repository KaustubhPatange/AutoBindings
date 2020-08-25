package com.kpstv.processor.utils

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.util.ElementFilter
import kotlin.reflect.KClass

inline fun <reified T : Annotation> Element.getElementFromAnnotation(): ExecutableElement? {
    ElementFilter.methodsIn(enclosedElements).forEach {
        if (it.getAnnotation(T::class.java) != null)
            return it
    }
    return null
}

inline fun <reified T : Annotation> Element.getAnnotationClassValue(f: T.() -> KClass<*>) =
    try {
        getAnnotation(T::class.java).f()
        throw Exception("Expected to get a MirroredTypeException")
    } catch (e: MirroredTypeException) {
        e.typeMirror
    }