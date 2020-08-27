package com.kpstv.customannotations

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kpstv.library_annotations.OnBind
import com.kpstv.library_annotations.RecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_test.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // val c =
        val models = listOf(
            TestClass("name1"), TestClass("name2")
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
       // recyclerView.adapter = BindDemoAdapter(DemoAdapter(), models)
    }
}

data class TestClass(val name: String)

/*@RecyclerViewAdapter(R.layout.item_test, TestClass::class)
class DemoAdapter {
    private val TAG = javaClass.simpleName

    @OnBind
    fun bind(view: View, item: TestClass, position: Int) {
        Log.e(TAG, "View: $view, Title: ${item.name}, Position: $position")
        view.item_title.text = item.name
    }
}*/
