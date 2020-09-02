package com.kpstv.customannotations

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

/*@RecyclerViewListAdapter(dataSetType = TestClass::class)
class DemoAdapter {

}*/
