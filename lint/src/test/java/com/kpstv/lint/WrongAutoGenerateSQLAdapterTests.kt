package com.kpstv.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.kpstv.lint.detectors.TypeConvertDetector
import com.kpstv.lint.utils.Stubs
import org.junit.Test

class WrongAutoGenerateSQLAdapterTests {
    @Test
    fun wrongAutoGenerateSQLAdapterInterfaceTest() {
        val stubFile = LintDetectorTest.kotlin(
            """
            package com.kpstv.lint
            import com.kpstv.bindings.AutoGenerateSQLDelightAdapters
            import com.squareup.sqldelight.ColumnAdapter
            
            data class User(val name: String, val age: Int)
                        
            @AutoGenerateSQLDelightAdapters(using = ConverterType.KOTLIN_SERIALIZATION)
            abstract class SQLDelightAdapters {
                abstract fun dataConverter(): ColumnAdapter<User, String>
            }
        """
        ).indented()
        TestLintTask.lint().files(
            Stubs.CLASS_CONVERTER_TYPE,
            Stubs.COLUMNADAPTER_ANNOTATIONS,
            Stubs.CLASS_COLUMNADAPTER,
            stubFile
        )
            .issues(TypeConvertDetector.ISSUE_NO_INTERFACE)
            .run()
            .expect(
                """
                src/com/kpstv/lint/User.kt:7: Error: Class must be an interface [delightNoInterface]
                @AutoGenerateSQLDelightAdapters(using = ConverterType.KOTLIN_SERIALIZATION)
                ^
                1 errors, 0 warnings
                """.trimIndent()
            )
    }

    @Test
    fun wrongAutoGenerateSQLDelightAdapterInvalidReturnTypeTest() {
        val stubFile = LintDetectorTest.kotlin(
            """
            package com.kpstv.lint
            import com.kpstv.bindings.AutoGenerateSQLDelightAdapters
            import com.squareup.sqldelight.ColumnAdapter
            
            data class User(val name: String, val age: Int)
                        
            @AutoGenerateSQLDelightAdapters(using = ConverterType.KOTLIN_SERIALIZATION)
            interface SQLDelightAdapters {
                fun dataConverter(): ColumnAdapter<List<User>, Int>
            }
        """
        ).indented()
        TestLintTask.lint().files(
            Stubs.CLASS_CONVERTER_TYPE,
            Stubs.COLUMNADAPTER_ANNOTATIONS,
            Stubs.CLASS_COLUMNADAPTER,
            stubFile
        )
            .issues(TypeConvertDetector.ISSUE_WRONG_RETURN_TYPE)
            .run()
            .expect(
                """
                src/com/kpstv/lint/User.kt:9: Error: Return type of function must be of type ColumnAdapter<*, String> [delightWrongReturnType]
                    fun dataConverter(): ColumnAdapter<List<User>, Int>
                    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                1 errors, 0 warnings
                """.trimIndent()
            )
    }
}