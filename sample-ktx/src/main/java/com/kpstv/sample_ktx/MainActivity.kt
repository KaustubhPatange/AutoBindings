package com.kpstv.sample_ktx

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kpstv.library_annotations.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_layout.view.*

class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val models = listOf(Data("item1"), Data("item2"), Data("item3"), Data("item4"))

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BindTestAdapter(TestAdapter(), models)
    }
}

data class Data(val name: String)

@RecyclerViewAdapter(R.layout.item_layout, Data::class)
class TestAdapter() {

    @DiffContentSame
    fun contentSame(oldItem: Data, newItem: Data) = false

    @DiffItemSame
    fun itemSame(oldItem: Data, newItem: Data) = false

    @Bind
    fun bind(view: View, item: Data, position: Int) {
        view.item_title.text = item.name
    }

    @OnClick(R.id.item_title)
    fun onClick(context: Context, item: Data, position: Int) {

    }

    @OnLongClick(R.id.item_title)
    fun onLongClick(context: Context, item: Data, position: Int) {
    }
}
