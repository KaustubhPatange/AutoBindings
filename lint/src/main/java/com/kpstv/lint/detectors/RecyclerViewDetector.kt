package com.kpstv.lint.detectors

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClass
import com.kpstv.lint.Language
import com.kpstv.lint.Utils
import com.kpstv.lint.language
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.visitor.AbstractUastVisitor

class RecyclerViewDetector : Detector(), Detector.UastScanner {

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
            commonClassDetector(context, node)
            return super.visitClass(node)
        }

        override fun visitMethod(node: UMethod): Boolean {
            if (node.hasAnnotation(ANNOTATION_ONBIND)) {
                if (node.parameterList.isEmpty) reportInCorrectBindAnnotation(node)
                else {
                    val parameters =
                        node.uastParameters.joinToString { it.type.canonicalText }
                    if (!parameters.matches(DETECTOR_INCORRECT_BIND.toRegex()))
                        reportInCorrectBindAnnotation(node)
                }
            } else if (node.hasAnnotation(ANNOTATION_ONCLICK)) {
                if (node.parameterList.isEmpty) reportInCorrectOnClickAnnotation(node)
                else {
                    val parameters =
                        node.uastParameters.joinToString { it.type.canonicalText }
                    if (!parameters.matches(DETECTOR_INCORRECT_CLICK.toRegex()))
                        reportInCorrectOnClickAnnotation(node)
                }
            } else if (node.hasAnnotation(ANNOTATION_ONLONGCLICK)) {
                if (node.parameterList.isEmpty) reportInCorrectOnLongClickAnnotation(node)
                else {
                    val parameters =
                        node.uastParameters.joinToString { it.type.canonicalText }
                    if (!parameters.matches(DETECTOR_INCORRECT_CLICK.toRegex()))
                        reportInCorrectOnLongClickAnnotation(node)
                }
            } else if (node.hasAnnotation(ANNOTATION_ITEMVIEWTYPE)) {
                if (node.parameterList.isEmpty) reportInCorrectViewTypeAnnotation(node)
                else if (node.returnType?.canonicalText != "int")
                    reportInCorrectViewTypeAnnotation(node)
                else {
                    val parameters =
                        node.uastParameters.joinToString { it.type.canonicalText }
                    if (parameters != DETECTOR_INCORRECT_ITEMVIEWTYPE)
                        reportInCorrectViewTypeAnnotation(node)
                }
            } else if (node.hasAnnotation(ANNOTATION_DIFFITEMSAME)) {
                if (node.parameterList.isEmpty) reportInCorrectDiffItemSameAnnotation(node)
                else if (node.returnType?.canonicalText != "boolean")
                    reportInCorrectDiffItemSameAnnotation(node)
                else {
                    if (node.uastParameters.size != 2)
                        reportInCorrectDiffItemSameAnnotation(node)
                }
            } else if (node.hasAnnotation(ANNOTATION_DIFFCONTENTSAME)) {
                if (node.parameterList.isEmpty) reportInCorrectDiffContentSameAnnotation(node)
                else if (node.returnType?.canonicalText != "boolean")
                    reportInCorrectDiffContentSameAnnotation(node)
                else {
                    if (node.uastParameters.size != 2)
                        reportInCorrectDiffItemSameAnnotation(node)
                }
            }
            return super.visitMethod(node)
        }

        private fun commonClassDetector(context: JavaContext, clazz: UClass) {
            if (clazz.hasAnnotation(ANNOTATION_RECYCLERVIEW)) {
                val annotation = clazz.getAnnotation(ANNOTATION_RECYCLERVIEW)
                commonRecyclerViewChecks(clazz, annotation)
            } else if (clazz.hasAnnotation(ANNOTATION_RECYCLERVIEWLIST)) {
                val annotation = clazz.getAnnotation(ANNOTATION_RECYCLERVIEWLIST)
                commonRecyclerViewChecks(clazz, annotation)

                // Detect usage of diffCallback method
                if (!clazz.allMethods.any { it.hasAnnotation(ANNOTATION_DIFFITEMSAME) }) {
                    context.report(
                        issue = ISSUE_NO_DIFFITEMSAME,
                        scopeClass = clazz,
                        location = context.getNameLocation(clazz),
                        message = MESSAGE_ISSUE_NO_DIFFITEMSAME,
                        quickfixData = createFixForInsertMethod(
                            clazz = clazz,
                            name = "Add \"DiffItemSame\" method",
                            codeFix = "{\n@${ANNOTATION_DIFFITEMSAME}\nfun diffItemSame(${commonDiffParameters(clazz)}): Boolean { }"
                        )
                    )
                }
                if (!clazz.allMethods.any { it.hasAnnotation(ANNOTATION_DIFFCONTENTSAME) }) {
                    context.report(
                        issue = ISSUE_NO_DIFFCONTENTSAME,
                        scopeClass = clazz,
                        location = context.getNameLocation(clazz),
                        message = MESSAGE_ISSUE_NO_DIFFCONTENTSAME,
                        quickfixData = createFixForInsertMethod(
                            clazz = clazz,
                            name = "Add \"DiffContentSame\" method",
                            codeFix = "{\n@${ANNOTATION_DIFFCONTENTSAME}\nfun diffContentSame(${commonDiffParameters(clazz)}): Boolean { }"
                        )
                    )
                }
            }
        }

        private fun commonRecyclerViewChecks(clazz: UClass, annotation: PsiAnnotation?) {
            if (annotation?.hasAttribute("dataSetType") == false) {
                context.report(
                    issue = ISSUE_RECYCLERVIEW,
                    scopeClass = clazz,
                    location = context.getNameLocation(clazz),
                    message = "Annotation must have a `dataSetType` parameter",
                    quickfixData = createFixForDataSet(clazz, annotation)
                )
            }

            // Detect usage of OnBind method
            if (clazz.allMethods.none { it.hasAnnotation(ANNOTATION_ONBIND) }) {
                context.report(
                    issue = ISSUE_ON_BIND,
                    scopeClass = clazz,
                    location = context.getNameLocation(clazz),
                    message = "There must be at least one @OnBind() method",
                    quickfixData = createFixForInsertMethod(
                        clazz = clazz,
                        name = "Add \"OnBind\" method",
                        codeFix = "{\n@${ANNOTATION_ONBIND}(layoutId = )\nfun bind($CORRECTED_BIND_PARAMETERS) { }"
                    )
                )
            }
        }

        private fun reportInCorrectDiffContentSameAnnotation(node: UMethod) {
            context.report(
                issue = ISSUE_INCORRECT_DIFFCONTENTSAME,
                scope = node.context,
                location = context.getLocation(node),
                message = MESSAGE_ISSUE_INCORRECT_DIFFCONTENTSAME,
                quickfixData = commonCorrect("${node.name}(${commonDiffParameters(node.containingClass)}): Boolean {")
            )
        }

        private fun reportInCorrectDiffItemSameAnnotation(node: UMethod) {
            context.report(
                issue = ISSUE_INCORRECT_DIFFITEMSAME,
                scope = node.context,
                location = context.getLocation(node),
                message = MESSAGE_ISSUE_INCORRECT_DIFFITEMSAME,
                quickfixData = commonCorrect("${node.name}(${commonDiffParameters(node.containingClass)}): Boolean {")
            )
        }

        private fun reportInCorrectViewTypeAnnotation(node: UMethod) {
            context.report(
                issue = ISSUE_INCORRECT_ITEMVIEWTYPE,
                scope = node.context,
                location = context.getLocation(node),
                message = MESSAGE_ISSUE_INCORRECT_VIEWTYPE,
                quickfixData = commonCorrect("${node.name}(position: Int): Int {")
            )
        }

        private fun reportInCorrectOnClickAnnotation(node: UMethod) {
            context.report(
                issue = ISSUE_INCORRECT_ONCLICK,
                scope = node.context,
                location = context.getLocation(node),
                message = MESSAGE_ISSUE_INCORRECT_ONCLICK,
                quickfixData = commonCorrect("${node.name}(context: android.content.Context, item: Any, position: Int) {")
            )
        }

        private fun reportInCorrectOnLongClickAnnotation(node: UMethod) {
            context.report(
                issue = ISSUE_INCORRECT_ONLONGCLICK,
                scope = node.context,
                location = context.getLocation(node),
                message = MESSAGE_ISSUE_INCORRECT_ONLONGCLICK,
                quickfixData = commonCorrect("${node.name}(context: android.content.Context, item: Any, position: Int) {")
            )
        }

        private fun reportInCorrectBindAnnotation(node: UMethod) {
            context.report(
                issue = ISSUE_INCORRECT_BIND,
                scope = node.context,
                location = context.getLocation(node),
                message = MESSAGE_ISSUE_INCORRECT_BIND,
                quickfixData = commonCorrect("${node.name}($CORRECTED_BIND_PARAMETERS) {")
            )
        }

        private fun createFixForInsertMethod(
            clazz: UClass,
            name: String,
            codeFix: String
        ): LintFix {
            return LintFix.create()
                .replace()
                .name(name)
                .pattern("${Utils.getSimpleName(clazz.qualifiedName)}(.*)")
                .with(codeFix)
                .reformat(true)
                .shortenNames()
                .range(
                    context.getRangeLocation(
                        clazz.navigationElement,
                        0,
                        clazz.textLength
                    )
                )
                .build()
        }

        private fun createFixForDataSet(
            clazz: UClass,
            annotation: PsiAnnotation?
        ): LintFix {
            val fix = "(dataSetType = Any::class)"
            val className = Utils.getSimpleName(annotation?.qualifiedName)
            return LintFix.create()
                .replace()
                .name("Add \"dataSetType\" parameter")
                .pattern("$className(.*)")
                .with(fix)
                .range(context.getLocation(clazz.navigationElement))
                .shortenNames()
                .build()
        }

        private fun commonCorrect(with: String): LintFix {
            return LintFix.create()
                .replace()
                .name("Add method parameters")
                .pattern(MATCHER_FUNCTION)
                .shortenNames()
                .reformat(true)
                .with(with)
                .robot(true)
                .independent(true)
                .build()
        }

        private fun commonDiffParameters(clazz: PsiClass?): String {
            val dataClass = Utils.findAnnotationValue(clazz, "dataSetType")
                .replace("::class", "")
            return "oldItem: $dataClass, newItem: $dataClass"
        }
    }

    companion object {
        const val CORRECTED_BIND_PARAMETERS = "view: android.view.View, item: Any, position: Int"

        const val MATCHER_FUNCTION = "fun ((.*?)\\{)"

        const val MESSAGE_ISSUE_NO_DIFFITEMSAME = "@DiffItemSame method is not implemented."
        const val MESSAGE_ISSUE_NO_DIFFCONTENTSAME = "@DiffContentSame method is not implemented."
        const val MESSAGE_ISSUE_INCORRECT_DIFFCONTENTSAME =
            "@DiffContentSame method is implemented incorrectly."
        const val MESSAGE_ISSUE_INCORRECT_DIFFITEMSAME =
            "@DiffItemSame method is implemented incorrectly."
        const val MESSAGE_ISSUE_INCORRECT_BIND = "@OnBind method is implemented incorrectly."
        const val MESSAGE_ISSUE_INCORRECT_VIEWTYPE =
            "@ItemViewType method is implemented incorrectly."
        const val MESSAGE_ISSUE_INCORRECT_ONCLICK =
            "@Onclick method parameters are implemented wrong."
        const val MESSAGE_ISSUE_INCORRECT_ONLONGCLICK =
            "@OnLongClick method parameters are implemented wrong."

        const val DETECTOR_INCORRECT_DIFFCALLBACK = "(.*?), (.*?)"
        const val DETECTOR_INCORRECT_ITEMVIEWTYPE = "int"
        const val DETECTOR_INCORRECT_BIND = "android.view.View, (.*?), int"
        const val DETECTOR_INCORRECT_CLICK =
            "android.content.Context, (.*?), int"

        const val ANNOTATION_RECYCLERVIEW = "com.kpstv.bindings.RecyclerViewAdapter"
        const val ANNOTATION_RECYCLERVIEWLIST =
            "com.kpstv.bindings.RecyclerViewListAdapter"
        const val ANNOTATION_DIFFITEMSAME = "com.kpstv.bindings.DiffItemSame"
        const val ANNOTATION_DIFFCONTENTSAME = "com.kpstv.bindings.DiffContentSame"
        const val ANNOTATION_ONCLICK = "com.kpstv.bindings.OnClick"
        const val ANNOTATION_ONLONGCLICK = "com.kpstv.bindings.OnLongClick"
        const val ANNOTATION_ONBIND = "com.kpstv.bindings.OnBind"
        const val ANNOTATION_ITEMVIEWTYPE = "com.kpstv.bindings.ItemViewType"

        private val IMPLEMENTATION = Implementation(
            RecyclerViewDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE_ON_BIND: Issue = Issue
            .create(
                id = "onBindNeeded",
                briefDescription = "At least one @OnBind method must exist.",
                explanation = """
                An adapter without the definition of OnBindViewHolder can lead to 
                failure in compilation.
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 9,
                severity = Severity.WARNING,
                androidSpecific = true,
                implementation = IMPLEMENTATION
            )
        val ISSUE_INCORRECT_BIND: Issue = Issue
            .create(
                id = "incorrectBindImplementation",
                briefDescription = MESSAGE_ISSUE_INCORRECT_BIND,
                explanation = """
                    The OnBind method is implemented incorrectly which can lead to 
                    failure in compilation.
            """.trimIndent(),
                category = Category.CORRECTNESS,
                priority = 10,
                severity = Severity.ERROR,
                androidSpecific = true,
                implementation = IMPLEMENTATION
            )
        val ISSUE_RECYCLERVIEW: Issue = Issue.create(
            id = "recyclerViewParameterMissing",
            briefDescription = "No parameter provided for data class.",
            explanation = """
                There must be a parameter for data class to be provided
                explicitly, otherwise it will be replaced with Any.class.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 9,
            severity = Severity.WARNING,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )
        val ISSUE_INCORRECT_ONCLICK: Issue = Issue.create(
            id = "incorrectOnClickImplementation",
            briefDescription = MESSAGE_ISSUE_INCORRECT_ONCLICK,
            explanation = """
                    The method is implemented incorrectly which can lead to 
                    failure in compilation.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )
        val ISSUE_INCORRECT_ONLONGCLICK: Issue = Issue.create(
            id = "incorrectOnLongClickImplementation",
            briefDescription = MESSAGE_ISSUE_INCORRECT_ONLONGCLICK,
            explanation = """
                    The method is implemented incorrectly which can lead to 
                    failure in compilation.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )

        val ISSUE_INCORRECT_ITEMVIEWTYPE: Issue = Issue.create(
            id = "incorrectItemViewTypeImplementation",
            briefDescription = MESSAGE_ISSUE_INCORRECT_VIEWTYPE,
            explanation = """
                    The method is implemented incorrectly which can lead to 
                    failure in compilation.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )

        val ISSUE_INCORRECT_DIFFITEMSAME: Issue = Issue.create(
            id = "incorrectDiffItemSameCallback",
            briefDescription = MESSAGE_ISSUE_INCORRECT_DIFFITEMSAME,
            explanation = """
                    The method is implemented incorrectly which can lead to 
                    failure in compilation.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )
        val ISSUE_INCORRECT_DIFFCONTENTSAME: Issue = Issue.create(
            id = "incorrectDiffItemContentCallback",
            briefDescription = MESSAGE_ISSUE_INCORRECT_DIFFCONTENTSAME,
            explanation = """
                    The method is implemented incorrectly which can lead to 
                    failure in compilation.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )

        val ISSUE_NO_DIFFITEMSAME: Issue = Issue.create(
            id = "noDiffItemSameCallback",
            briefDescription = MESSAGE_ISSUE_NO_DIFFITEMSAME,
            explanation = """
                    The method is not implemented which can lead to
                    failure in compilation.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )
        val ISSUE_NO_DIFFCONTENTSAME: Issue = Issue.create(
            id = "noDiffContentSameCallback",
            briefDescription = MESSAGE_ISSUE_NO_DIFFCONTENTSAME,
            explanation = """
                    The method is not implemented which can lead to
                    failure in compilation.
            """.trimIndent(),
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            androidSpecific = true,
            implementation = IMPLEMENTATION
        )
    }
}