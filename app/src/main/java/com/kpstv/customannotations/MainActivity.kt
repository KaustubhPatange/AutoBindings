package com.kpstv.customannotations

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kpstv.library_annotations.DiffItemSame
import com.kpstv.library_annotations.OnBind
import com.kpstv.library_annotations.RecyclerViewAdapter
import com.kpstv.library_annotations.RecyclerViewListAdapter
import kotlinx.android.synthetic.main.activity_main.*

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

@RecyclerViewAdapter(dataSetType = Any::class)
class DemoAdapter {
    private val TAG = javaClass.simpleName
}
