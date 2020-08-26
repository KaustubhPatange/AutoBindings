package com.kpstv.sample_ktx

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
        recyclerView.adapter = BindTestAdapter(TestAdapter())
    }
}

data class Data(val name: String)

@RecyclerViewListAdapter(R.layout.item_layout, Data::class)
class TestAdapter {

    @DiffContentSame
    fun contentSame(oldItem: Data, newItem: Data) = false

    @DiffItemSame
    fun itemSame(oldItem: Data, newItem: Data) = false

    @Bind
    fun bind(view: View, item: Data, position: Int) {}
    /*@Bind
    fun bind(view: View, item: Data, position: Int) = with(view.context) {
        view.item_title.text = item.name
        view.item_title.setOnClickListener {
            Toast.makeText(this, "Click position $position", Toast.LENGTH_SHORT).show()
        }
    }*/
}
