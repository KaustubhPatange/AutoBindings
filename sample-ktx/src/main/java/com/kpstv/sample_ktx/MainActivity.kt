package com.kpstv.sample_ktx

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kpstv.bindings.*
import com.kpstv.sample_ktx.Utils.createRandomImageUrl
import com.squareup.moshi.JsonClass
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_small_layout.view.*
import kotlinx.serialization.Serializable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val models = listOf(
            Data(createRandomImageUrl()),
            Data(createRandomImageUrl()),
            Data(createRandomImageUrl()),
            Data(createRandomImageUrl()),
            Data(createRandomImageUrl()),
            Data(createRandomImageUrl())
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BindTestAdapter(TestAdapter()).apply { submitList(models) }
    }
}

@JsonClass(generateAdapter = true)
data class Clip(val p1: String, val p2: Boolean)

@JsonClass(generateAdapter = true)
data class User(val name: String, val map: Map<Int, Clip>)

/**
 * POJO class
 */
@Serializable
@JsonClass(generateAdapter = true)
@AutoGeneratePairConverter(keyClass = String::class, using = ConverterType.MOSHI)
data class Data(val name: String, val visible: Boolean = true)

/**
 * An example of using RecyclerView's modern ListAdapter
 */
@RecyclerViewListAdapter(dataSetType = Data::class)
class TestAdapter {

    @DiffContentSame
    fun diffContentSame(oldItem: Data, newItemSame: Data) = oldItem == newItemSame

    @DiffItemSame
    fun diffItemSame(oldItem: Data, newItemSame: Data) = oldItem.name == newItemSame.name

    @GlideLoadArray(
        GlideLoad(R.id.item_small_image, "name", transformationType = ImageTransformationType.CIRCLE_CROP)
    )
    @OnBind(R.layout.item_small_layout)
    fun bind(view: View, item: Data, position: Int) {
        view.item_title.text = item.name
    }

    @GlideLoadArray(
        GlideLoad(R.id.item_big_image, "name", transformationType = ImageTransformationType.CENTER_CROP)
    )
    @OnBind(R.layout.item_big_layout, 2)
    fun bind2(view: View, item: Data, position: Int) {
        // blank af
    }

    /** Alternate layout swaps logic */
    @ItemViewType
    fun viewType(position: Int): Int = position % 2 * 2

    /** Single click is enabled for small layout */
    @OnClick(R.id.mainLayout, false)
    fun onClick(context: Context, item: Data, position: Int) = with(context) {
        Toast.makeText(this, "Click, Position: $position", Toast.LENGTH_SHORT).show()
    }

    /**  Long click is enabled for big layout */
    @OnLongClick(R.id.mainLayout, viewType = 2)
    fun onLongClick(context: Context, item: Data, position: Int) = with(context) {
        Toast.makeText(this, "LongClick, Position: $position", Toast.LENGTH_SHORT).show()
    }
}
