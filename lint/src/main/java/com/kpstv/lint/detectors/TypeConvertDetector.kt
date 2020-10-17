package com.kpstv.lint.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.visitor.AbstractUastVisitor

class TypeConvertDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes() = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) =
        object : UElementHandler() {
            override fun visitClass(node: UClass) {
                node.accept(
                    VisitorPattern(
                        context
                    )
                )
            }
        }

    class VisitorPattern(private val context: JavaContext) : AbstractUastVisitor() {
        override fun visitClass(node: UClass): Boolean {
            if (node.hasAnnotation(ANNOTATION_CONVERTER)) {
                val converterAnnotation = node.getAnnotation(ANNOTATION_CONVERTER)
                commonSerializationReport(node, converterAnnotation)
            }
            if (node.hasAnnotation(ANNOTATION_LIST_CONVERTER)) {
                val converterAnnotation = node.getAnnotation(ANNOTATION_LIST_CONVERTER)
                if (converterAnnotation?.findAttributeValue("using")?.text == "ConverterType.MOSHI") {
                    if (!node.hasAnnotation(ANNOTATION_JSONCLASS))
                        reportNoJsonClass(node)
                    else {
                        val jsonClassAnnotation = node.getAnnotation(ANNOTATION_JSONCLASS)
                        if (jsonClassAnnotation?.findAttributeValue("generateAdapter")?.text != "true")
                            reportNoJsonClass(node)
                    }
                }
                commonSerializationReport(node, converterAnnotation)
            }
            return super.visitClass(node)
        }

        private fun reportNoJsonClass(node: UClass) {
            context.report(
                issue = ISSUE_NO_JSONCLASS,
                scopeClass = node,
                location = context.getNameLocation(node),
                message = "Class must be annotated with @JsonClass(generateAdapter = true)"
            )
        }

        private fun commonSerializationReport(node: UClass, converterAnnotation: PsiAnnotation?) {
            if (converterAnnotation?.findAttributeValue("using")?.text == "ConverterType.KOTLIN_SERIALIZATION") {
                if (!node.hasAnnotation(ANNOTATION_SERIALIZABLE))
                    context.report(
                        issue = ISSUE_NO_SERIALIZABLE,
                        scopeClass = node,
                        location = context.getNameLocation(node),
                        message = "Class must be annotated with @Serializable."
                    )
            }
        }
    }

    companion object {
        const val ANNOTATION_CONVERTER = "com.kpstv.bindings.AutoGenerateConverter"
        const val ANNOTATION_LIST_CONVERTER = "com.kpstv.bindings.AutoGenerateListConverter"
        const val ANNOTATION_SERIALIZABLE = "kotlinx.serialization.Serializable"
        const val ANNOTATION_JSONCLASS = "com.squareup.moshi.JsonClass"

        private val IMPLEMENTATION = Implementation(
            TypeConvertDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE_NO_SERIALIZABLE: Issue = Issue
            .create(
                id = "kotlinSerialization",
                briefDescription = "Class must be annotated with @Serializable.",
                explanation = """
                Kotlin serialization needs classes to be annotated with @Serializable
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 9,
                severity = Severity.WARNING,
                androidSpecific = true,
                implementation = IMPLEMENTATION
            )

        val ISSUE_NO_JSONCLASS: Issue = Issue
            .create(
                id = "noJsonClass",
                briefDescription = "Class must be annotated with @JsonClass(generateAdapter = true)",
                explanation = """
                This is needed otherwise the program will create runtime exceptions.
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 9,
                severity = Severity.WARNING,
                androidSpecific = true,
                implementation = IMPLEMENTATION
            )
    }
}