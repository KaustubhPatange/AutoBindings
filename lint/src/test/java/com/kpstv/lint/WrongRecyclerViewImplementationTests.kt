package com.kpstv.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.android.tools.lint.checks.infrastructure.TestMode
import com.kpstv.lint.detectors.RecyclerViewDetector
import com.kpstv.lint.utils.Stubs
import org.junit.Test

class WrongRecyclerViewImplementationTests {

    @Test
    fun onBindTest() {
        val stubFile = kotlin(
            """
            package com.kpstv.lint
            import com.kpstv.bindings.RecyclerViewAdapter
            @RecyclerViewAdapter
            class TestAdapter {
            }
        """
        ).indented()
        lint().files(Stubs.RECYCLERVIEW_ANNOTATION, Stubs.LAYOUT_RES_ANNOTATION, Stubs.ONBIND_ANNOTATION, stubFile)
            .issues(RecyclerViewDetector.ISSUE_ON_BIND)
            .run()
            .expect(
                """
                src/com/kpstv/lint/TestAdapter.kt:4: Warning: There must be at least one @OnBind() method [onBindNeeded]
                class TestAdapter {
                      ~~~~~~~~~~~
                0 errors, 1 warnings
            """.trimIndent(),
                testMode = TestMode.DEFAULT
            )
    }

    @Test
    fun onRecyclerViewDataSetParameterNotEmptyTest() {
        val stubFile = kotlin(
            """
                package com.kpstv.lint
                import com.kpstv.bindings.RecyclerViewAdapter
                @RecyclerViewAdapter()
                class TestAdapter {
                }
            """
        ).indented()
        lint().files(Stubs.RECYCLERVIEW_ANNOTATION, stubFile)
            .issues(RecyclerViewDetector.ISSUE_RECYCLERVIEW)
            .run()
            .expect(
                """
                src/com/kpstv/lint/TestAdapter.kt:4: Warning: Annotation must have a dataSetType parameter [recyclerViewParameterMissing]
                class TestAdapter {
                      ~~~~~~~~~~~
                0 errors, 1 warnings
            """.trimIndent()
            )
    }

    @Test
    fun onBindMethodImplementedIncorrectlyTest() {
        val stubFile = kotlin(
            """
                package com.kpstv.lint
                
                import com.kpstv.bindings.RecyclerViewAdapter
                import com.kpstv.bindings.OnBind
                
                @RecyclerViewAdapter
                class TestAdapter {
                    @OnBind(layoutId = 12)
                    fun bind() {}
                }
            """
        ).indented()
        lint().files(
            Stubs.CLASS_VIEW,
            Stubs.RECYCLERVIEW_ANNOTATION,
            Stubs.ONBIND_ANNOTATION,
            stubFile
        )
            .issues(RecyclerViewDetector.ISSUE_INCORRECT_BIND)
            .allowCompilationErrors()
            .run()
            .expect(
                """
                src/com/kpstv/lint/TestAdapter.kt:8: Error: @OnBind method is implemented incorrectly. [incorrectBindImplementation]
                    @OnBind(layoutId = 12)
                    ^
                1 errors, 0 warnings
            """.trimIndent()
            )
    }

    @Test
    fun onClickMethodImplementedIncorrectlyTest() {
        val stubFile = kotlin(
            """
                package com.kpstv.lint
                
                import com.kpstv.bindings.OnClick
                import com.kpstv.bindings.RecyclerViewAdapter

                @RecyclerViewAdapter
                class TestAdapter {
                    @OnClick(21)
                    fun click() { }
                } 
            """
        ).indented()
        lint().files(
            Stubs.RECYCLERVIEW_ANNOTATION,
            Stubs.ONCLICK_ANNOTATION,
            stubFile
        )
            .issues(RecyclerViewDetector.ISSUE_INCORRECT_ONCLICK)
            .run()
            .expect(
                """
                src/com/kpstv/lint/TestAdapter.kt:8: Error: @Onclick method parameters are implemented wrong. [incorrectOnClickImplementation]
                    @OnClick(21)
                    ^
                1 errors, 0 warnings
            """.trimIndent()
            )
    }

    @Test
    fun itemViewTypeImplementedIncorrectlyTest() {
        val stubFile = kotlin(
            """
                package com.kpstv.lint
                
                import com.kpstv.bindings.ItemViewType
                import com.kpstv.bindings.RecyclerViewAdapter

                @RecyclerViewAdapter
                class TestAdapter {
                    @ItemViewType
                    fun viewType() { }
                } 
            """
        ).indented()
        lint().files(
            Stubs.RECYCLERVIEW_ANNOTATION,
            Stubs.ITEMVIEWTYPE_ANNOTATION,
            stubFile
        ).issues(RecyclerViewDetector.ISSUE_INCORRECT_ITEMVIEWTYPE)
            .run()
            .expect("""
                src/com/kpstv/lint/TestAdapter.kt:8: Error: @ItemViewType method is implemented incorrectly. [incorrectItemViewTypeImplementation]
                    @ItemViewType
                    ^
                1 errors, 0 warnings
            """.trimIndent())
    }
}