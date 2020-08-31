package com.kpstv.lint

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.kpstv.lint.detectors.RecyclerViewDetector
import com.kpstv.lint.utils.Stubs
import org.junit.Test

class CorrectRecyclerViewListImplementationTests {
    @Test
    fun correctImplementationOfDiffCallbackTest() {
        val stubFile = TestFiles.kotlin(
            """
                package com.kpstv.lint
                
                import com.kpstv.library_annotations.DiffItemSame
                import com.kpstv.library_annotations.DiffContentSame
                import com.kpstv.library_annotations.RecyclerViewListAdapter
                import kotlin.reflect.KClass

                @RecyclerViewListAdapter
                class TestAdapter {
                    @DiffItemSame
                    fun diffItemSame(o: KClass<*>, n: KClass<*>): Boolean = false
                    
                    @DiffContentSame
                     fun diffContentSame(o: KClass<*>, n: KClass<*>): Boolean = false
                } 
            """
        ).indented()
        TestLintTask.lint().files(
            Stubs.DIFFCONTENTSAME_ANNOTATION,
            Stubs.DIFFITEMSAME_ANNOTATION,
            Stubs.RECYCLERVIEWLIST_ANNOTATION,
            stubFile
        ).issues(
            RecyclerViewDetector.ISSUE_NO_DIFFITEMSAME,
            RecyclerViewDetector.ISSUE_NO_DIFFCONTENTSAME,
            RecyclerViewDetector.ISSUE_INCORRECT_DIFFITEMSAME,
            RecyclerViewDetector.ISSUE_INCORRECT_DIFFCONTENTSAME
        )
            .run()
            .expect("No warnings.")
    }
}