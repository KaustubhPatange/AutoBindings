package com.kpstv.lint.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiAnnotation
import com.kpstv.bindings.AutoGenerateConverter
import com.kpstv.bindings.AutoGenerateListConverter
import com.kpstv.bindings.AutoGenerateSQLDelightAdapters
import com.kpstv.lint.Utils
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.toUElement
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
                        reportNoJsonClass(node, converterAnnotation)
                    else {
                        val jsonClassAnnotation = node.getAnnotation(ANNOTATION_JSONCLASS)
                        if (jsonClassAnnotation?.findAttributeValue("generateAdapter")?.text != "true")
                            reportNoJsonClass(node, converterAnnotation)
                    }
                }
                commonSerializationReport(node, converterAnnotation)
            }
            if (node.hasAnnotation(ANNOTATION_COLUMN_ADAPTER)) {
                if (!node.isInterface)
                    context.report(
                        issue = ISSUE_NO_INTERFACE,
                        scopeClass = node,
                        location = context.getLocation(node.navigationElement),
                        message = MSG_NO_INTERFACE
                    )
            }
            return super.visitClass(node)
        }

        override fun visitMethod(node: UMethod): Boolean {
            val parentNode = node.parent.toUElement()
            if (parentNode is UClass && parentNode.hasAnnotation(ANNOTATION_COLUMN_ADAPTER)) {
                val split = INVALID_RETURN_TYPE_MATCHER.toRegex()
                    .matchEntire(node.returnType?.canonicalText ?: "")
                    ?.groups?.get(2)
                    ?.value?.split(",")
                if (split?.last()?.trim() != CLASS_STRING)
                    context.report(
                        issue = ISSUE_WRONG_RETURN_TYPE,
                        location = context.getLocation(node),
                        message = MSG_INVALID_RETURN_TYPE
                    )
            }
            return super.visitMethod(node)
        }

        private fun reportNoJsonClass(node: UClass, converterAnnotation: PsiAnnotation?) {
            val className = Utils.getSimpleName(converterAnnotation?.qualifiedName)
            context.report(
                issue = ISSUE_NO_JSONCLASS,
                scopeClass = node,
                location = context.getLocation(node.navigationElement),
                message = "Class must be annotated with @JsonClass(generateAdapter = true).",
                quickfixData = LintFix.create()
                    .replace()
                    .name("Add @JsonClass(generateAdapter = true) annotation")
                    .beginning()
                    .with("@${ANNOTATION_JSONCLASS}(generateAdapter = true)")
                    .reformat(true)
                    .range(context.getLocation(node.navigationElement))
                    .shortenNames()
                    .build()
            )
        }

        private fun commonSerializationReport(node: UClass, converterAnnotation: PsiAnnotation?) {
            if (converterAnnotation?.findAttributeValue("using")?.text == "ConverterType.KOTLIN_SERIALIZATION") {
                if (!node.hasAnnotation(ANNOTATION_SERIALIZABLE)) {
                    val className = Utils.getSimpleName(converterAnnotation.qualifiedName)
                    context.report(
                        issue = ISSUE_NO_SERIALIZABLE,
                        scopeClass = node,
                        location = context.getLocation(node.navigationElement),
                        message = "Class must be annotated with @Serializable.",
                        quickfixData = LintFix.create()
                            .replace()
                            .name("Add @Serializable annotation")
                            .beginning()
                            .with("@${ANNOTATION_SERIALIZABLE}")
                            .reformat(true)
                            .range(context.getLocation(node.navigationElement))
                            .shortenNames()
                            .build()
                    )
                }
            }
        }
    }

    companion object {
        private const val ANNOTATION_CONVERTER = "com.kpstv.bindings.AutoGenerateConverter"
        private const val ANNOTATION_LIST_CONVERTER = "com.kpstv.bindings.AutoGenerateListConverter"
        private const val ANNOTATION_COLUMN_ADAPTER = "com.kpstv.bindings.AutoGenerateSQLDelightAdapters"
        const val ANNOTATION_SERIALIZABLE = "kotlinx.serialization.Serializable"
        const val ANNOTATION_JSONCLASS = "com.squareup.moshi.JsonClass"

        const val CLASS_STRING = "java.lang.String"
        const val INVALID_RETURN_TYPE_MATCHER =
            "com\\.squareup\\.sqldelight\\.ColumnAdapter(\\s+)?<(.*)>"

        private const val MSG_NO_INTERFACE = "Class must be an interface"
        private const val MSG_INVALID_RETURN_TYPE =
            "Return type of function must be of type ColumnAdapter<*, String>"

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
        val ISSUE_NO_INTERFACE: Issue = Issue
            .create(
                id = "delightNoInterface",
                briefDescription = MSG_NO_INTERFACE,
                explanation = """
                This is needed otherwise the program will create runtime exceptions.
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 10,
                severity = Severity.ERROR,
                androidSpecific = true,
                implementation = IMPLEMENTATION
            )
        val ISSUE_WRONG_RETURN_TYPE: Issue = Issue
            .create(
                id = "delightWrongReturnType",
                briefDescription = MSG_INVALID_RETURN_TYPE,
                explanation = """
                This is needed otherwise the program will create runtime exceptions.
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 10,
                severity = Severity.ERROR,
                androidSpecific = true,
                implementation = IMPLEMENTATION
            )
    }
}