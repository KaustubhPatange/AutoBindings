package com.kpstv.sample_ktx

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kpstv.library_annotations.*
import com.kpstv.sample_ktx.Utils.createRandomImageUrl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_layout.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val models = listOf(
            Data(createRandomImageUrl()),
            Data(createRandomImageUrl()),
            Data(createRandomImageUrl()),
            Data(createRandomImageUrl())
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DemoAdapter(TestAdapter()).apply { submitList(models) }
    }
}

/**
 * POJO class
 */
data class Data(val name: String, val visible: Boolean = true)

/**
 * An example of using RecyclerView's modern ListAdapter
 */
@RecyclerViewListAdapter(R.layout.item_layout, Data::class)
class TestAdapter {

    @DiffContentSame
    fun diffContentSame(oldItem: Data, newItemSame: Data) = oldItem == newItemSame

    @DiffItemSame
    fun diffItemSame(oldItem: Data, newItemSame: Data) = oldItem.name == oldItem.name

    @GlideLoadArray(
        GlideLoad(R.id.imageView, "name", transformationType = ImageTransformationType.CIRCLE_CROP)
    )
    @OnBind
    fun bind(view: View, item: Data, position: Int) {
        view.item_title.text = item.name
    }

    @OnClick(R.id.mainLayout, false)
    fun onClick(context: Context, item: Data, position: Int) = with(context) {
        Toast.makeText(this, "Click, Position: $position", Toast.LENGTH_SHORT).show()
    }

    @OnLongClick(R.id.mainLayout)
    fun onLongClick(context: Context, item: Data, position: Int) = with(context) {
        Toast.makeText(this, "LongClick, Position: $position", Toast.LENGTH_SHORT).show()
    }
}
