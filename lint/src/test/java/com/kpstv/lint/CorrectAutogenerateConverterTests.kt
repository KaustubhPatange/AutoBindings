package com.kpstv.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.kpstv.lint.detectors.TypeConvertDetector
import com.kpstv.lint.utils.Stubs
import org.junit.Test

class CorrectAutogenerateConverterTests {
    @Test
    fun correctSerializationTest() {
        val stubFile = LintDetectorTest.kotlin(
            """
            package com.kpstv.lint
            import com.kpstv.bindings.AutoGenerateConverter
            import com.kpstv.bindings.ConverterType
            import kotlinx.serialization.Serializable
            
            @Serializable
            @AutoGenerateConverter(using = ConverterType.KOTLIN_SERIALIZATION)
            data class User(val name: String)
        """
        ).indented()
        TestLintTask.lint().files(
            Stubs.CLASS_CONVERTER_TYPE,
            Stubs.CLASS_KOTLIN_SERIALIZABLE,
            Stubs.LISTCONVERTER_ANNOTATIONS,
            Stubs.CONVERTER_ANNOTATIONS,
            stubFile
        )
            .issues(TypeConvertDetector.ISSUE_NO_SERIALIZABLE)
            .run()
            .expect(
                "No warnings."
            )
    }

    @Test
    fun correctJsonClassTest() {
        val stubFile = LintDetectorTest.kotlin(
            """
            package com.kpstv.lint
            import com.kpstv.bindings.AutoGenerateListConverter
            import com.kpstv.bindings.ConverterType
            import com.squareup.moshi.JsonClass
            
            @JsonClass(generateAdapter = false)
            @AutoGenerateListConverter(using = ConverterType.MOSHI)
            data class User(val name: String)
        """
        ).indented()
        TestLintTask.lint().files(
            Stubs.CLASS_CONVERTER_TYPE,
            Stubs.CLASS_KOTLIN_SERIALIZABLE,
            Stubs.LISTCONVERTER_ANNOTATIONS,
            Stubs.CONVERTER_ANNOTATIONS,
            Stubs.CLASS_JSONCLASS,
            stubFile
        )
            .issues(TypeConvertDetector.ISSUE_NO_JSONCLASS)
            .run()
            .expect(
               "No warnings."
            )
    }
}