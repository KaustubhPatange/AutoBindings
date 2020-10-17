package com.kpstv.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.kpstv.lint.detectors.TypeConvertDetector
import com.kpstv.lint.utils.Stubs
import org.junit.Test

class WrongAutogenerateConverterTests {
    @Test
    fun wrongSerializationTest() {
        val stubFile = kotlin(
            """
            package com.kpstv.lint
            import com.kpstv.bindings.AutoGenerateConverter
            import com.kpstv.bindings.ConverterType
            
            @AutoGenerateConverter(using = ConverterType.KOTLIN_SERIALIZATION)
            data class User(val name: String)
        """
        ).indented()
        lint().files(
            Stubs.CLASS_CONVERTER_TYPE,
            Stubs.CLASS_KOTLIN_SERIALIZABLE,
            Stubs.LISTCONVERTER_ANNOTATIONS,
            Stubs.CONVERTER_ANNOTATIONS,
            stubFile
        )
            .issues(TypeConvertDetector.ISSUE_NO_SERIALIZABLE)
            .run()
            .expect(
                """
                src/com/kpstv/lint/User.kt:6: Warning: Class must be annotated with @Serializable. [kotlinSerialization]
                data class User(val name: String)
                           ~~~~
                0 errors, 1 warnings
                """.trimIndent()
            )
    }

    @Test
    fun wrongJsonClassTest() {
        val stubFile = kotlin(
            """
            package com.kpstv.lint
            import com.kpstv.bindings.AutoGenerateListConverter
            import com.kpstv.bindings.ConverterType
            
            @AutoGenerateListConverter(using = ConverterType.MOSHI)
            data class User(val name: String)
        """
        ).indented()
        lint().files(
            Stubs.CLASS_CONVERTER_TYPE,
            Stubs.CLASS_KOTLIN_SERIALIZABLE,
            Stubs.LISTCONVERTER_ANNOTATIONS,
            Stubs.CONVERTER_ANNOTATIONS,
            stubFile
        )
            .issues(TypeConvertDetector.ISSUE_NO_JSONCLASS)
            .run()
            .expect(
               """
                src/com/kpstv/lint/User.kt:6: Warning: Class must be annotated with @JsonClass(generateAdapter = true) [noJsonClass]
                data class User(val name: String)
                           ~~~~
                0 errors, 1 warnings
               """.trimIndent()
            )
    }
}