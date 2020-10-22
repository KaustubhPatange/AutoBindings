package com.kpstv.sample_ktx

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kpstv.bindings.*
import com.kpstv.sample_ktx.Utils.createRandomImageUrl
import com.kpstv.sample_ktx.Utils.createRandomTag
import com.kpstv.sample_ktx.data.Data
import com.kpstv.sample_ktx.data.mapToDomain
import com.kpstv.sample_ktx.databinding.ItemSmallLayoutBinding
import com.kpstv.samplektx.data.DataDomain
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * IRW, you should inject database using a dependency injection framework to avoid recreating of database.
         */
        val driver: SqlDriver = AndroidSqliteDriver(Database.Schema, this, "data.db")
        val database = Database(driver, DataDomainAdapter = DataDomain.Adapter(
            tagsAdapter = SQLDelightAdaptersImpl.tagsConverter
        ))

        val models = listOf(
            Data("Random1", createRandomImageUrl(), createRandomTag()),
            Data("Random2", createRandomImageUrl(), createRandomTag()),
            Data("Random3", createRandomImageUrl(), createRandomTag()),
            Data("Random4", createRandomImageUrl(), createRandomTag()),
            Data("Random5", createRandomImageUrl(), createRandomTag()),
            Data("Random6", createRandomImageUrl(), createRandomTag())
        )

        // Deleting all queries to simulate random data generate behaviour
        database.dataDomainQueries.deleteAll()

        /**
         * Insert all queries into database. This is to show working of [SQLDelightAdapters.tagsConverter] (if this would've failed then app will crash)
         * IRW, domain model must be fetched on app launched and should be map into base model class for further use.
         */
        models.mapToDomain().forEach {
            database.dataDomainQueries.insert(it)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BindTestAdapter(TestAdapter()).apply { submitList(models) }
    }
}

/**
 * An example of using RecyclerView's modern ListAdapter
 */
@RecyclerViewListAdapter(dataSetType = Data::class)
class TestAdapter {

    @DiffContentSame
    fun diffContentSame(oldItem: Data, newItemSame: Data) = oldItem == newItemSame

    @DiffItemSame
    fun diffItemSame(oldItem: Data, newItemSame: Data) = oldItem.imageUrl == newItemSame.imageUrl

    @GlideLoadArray(
        GlideLoad(R.id.item_small_image,
            "imageUrl",
            transformationType = ImageTransformationType.CIRCLE_CROP,
            errorRes = R.mipmap.ic_launcher)
    )
    @OnBind(R.layout.item_small_layout)
    fun bind(view: View, item: Data, position: Int) = with(ItemSmallLayoutBinding.bind(view)) {
        itemTitle.text = item.name
        itemTags.text = item.tags.joinToString(", ")
    }

    @GlideLoadArray(
        GlideLoad(R.id.item_big_image,
            "imageUrl",
            transformationType = ImageTransformationType.CENTER_CROP,
            errorRes = R.mipmap.ic_launcher)
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
