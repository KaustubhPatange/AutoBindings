package com.kpstv.lint

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.kpstv.lint.detectors.RecyclerViewDetector
import com.kpstv.lint.utils.Stubs
import org.junit.Test

class WrongRecyclerViewListImplementationTests {
    @Test
    fun noRecyclerViewDiffCallbackImplementationTest() {
        val stubFile = TestFiles.kotlin(
            """
                package com.kpstv.lint
                
                import com.kpstv.library_annotations.RecyclerViewListAdapter

                @RecyclerViewListAdapter
                class TestAdapter {
                } 
            """
        ).indented()
        TestLintTask.lint().files(
            Stubs.RECYCLERVIEWLIST_ANNOTATION,
            stubFile
        ).issues(RecyclerViewDetector.ISSUE_NO_DIFFITEMSAME, RecyclerViewDetector.ISSUE_NO_DIFFCONTENTSAME)
            .run()
            .expect(
                """
                src/com/kpstv/lint/TestAdapter.kt:6: Error: @DiffContentSame method is not implemented. [noDiffItemSameCallback]
                class TestAdapter {
                      ~~~~~~~~~~~
                src/com/kpstv/lint/TestAdapter.kt:6: Error: @DiffItemSame method is not implemented. [noDiffItemSameCallback]
                class TestAdapter {
                      ~~~~~~~~~~~
                2 errors, 0 warnings
            """.trimIndent()
            )
    }

    @Test
    fun incorrectRecyclerViewDiffCallbackImplementationTest() {
        val stubFile = TestFiles.kotlin(
            """
                package com.kpstv.lint
                
                import com.kpstv.library_annotations.DiffItemSame
                import com.kpstv.library_annotations.DiffContentSame
                import com.kpstv.library_annotations.RecyclerViewListAdapter

                @RecyclerViewListAdapter
                class TestAdapter {
                    @DiffItemSame
                    fun diffItemSame() {}
                    
                    @DiffContentSame
                    fun diffContentSame() {}
                } 
            """
        ).indented()
        TestLintTask.lint().files(
            Stubs.DIFFCONTENTSAME_ANNOTATION,
            Stubs.DIFFITEMSAME_ANNOTATION,
            Stubs.RECYCLERVIEWLIST_ANNOTATION,
            stubFile
        ).issues(
            RecyclerViewDetector.ISSUE_INCORRECT_DIFFCONTENTSAME,
            RecyclerViewDetector.ISSUE_INCORRECT_DIFFITEMSAME
        )
            .run()
            .expect(
                """
                src/com/kpstv/lint/TestAdapter.kt:12: Error: @DiffContentSame method is implemented incorrectly. [incorrectDiffItemContentCallback]
                    @DiffContentSame
                    ^
                src/com/kpstv/lint/TestAdapter.kt:9: Error: @DiffItemSame method is implemented incorrectly. [incorrectDiffItemSameCallback]
                    @DiffItemSame
                    ^
                2 errors, 0 warnings
            """.trimIndent()
            )
    }
}