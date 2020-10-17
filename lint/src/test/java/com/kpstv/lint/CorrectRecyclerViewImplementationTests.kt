package com.kpstv.lint

import com.android.tools.lint.checks.infrastructure.TestFiles
import com.android.tools.lint.checks.infrastructure.TestLintTask
import com.kpstv.lint.detectors.RecyclerViewDetector
import com.kpstv.lint.utils.Stubs
import org.junit.Test

class CorrectRecyclerViewImplementationTests {

    @Test
    fun correctImplementationOfRecyclerViewTest() {
        val stubFile = TestFiles.kotlin(
            """
                package com.kpstv.lint
                
                import com.kpstv.bindings.RecyclerViewAdapter
                import com.kpstv.bindings.OnBind
                import kotlin.reflect.KClass
                import android.view.View
                import com.kpstv.bindings.OnClick
                import com.kpstv.bindings.OnLongClick
                import com.kpstv.bindings.ItemViewType
                import android.content.Context
                data class TestClass(val name: String)
                @RecyclerViewAdapter(TestClass::class.java)
                class TestAdapter {
                    @OnBind(layoutId = 12)
                    fun bind(v: View, i: TestClass, p: Int) {}
                    
                    @OnClick(2)
                    fun onClick(c: Context, i: KClass<*>, p: Int) {}
                    
                    @OnLongClick(2)
                    fun onLongClick(c: Context, i: KClass<*>, p: Int) {}
                    
                    @ItemViewType
                    fun viewType(p: Int): Int = 0
                }
            """
        ).indented()
        TestLintTask.lint().files(
            Stubs.CLASS_CONTEXT,
            Stubs.CLASS_VIEW,
            Stubs.RECYCLERVIEW_ANNOTATION,
            Stubs.ONBIND_ANNOTATION,
            Stubs.ONCLICK_ANNOTATION,
            Stubs.ONLONGCLICK_ANNOTATION,
            Stubs.ITEMVIEWTYPE_ANNOTATION,
            stubFile
        )
            .issues(
                RecyclerViewDetector.ISSUE_INCORRECT_BIND,
                RecyclerViewDetector.ISSUE_RECYCLERVIEW,
                RecyclerViewDetector.ISSUE_ON_BIND,
                RecyclerViewDetector.ISSUE_INCORRECT_ONCLICK,
                RecyclerViewDetector.ISSUE_INCORRECT_ONLONGCLICK,
                RecyclerViewDetector.ISSUE_INCORRECT_ITEMVIEWTYPE
            )
            .run()
            .expect("No warnings.")
    }
}