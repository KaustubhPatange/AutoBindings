package com.kpstv.sample_ktx.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        TestDataClass::class
    ],
    version = 1
)
@TypeConverters(
    com.kpstv.sample_ktx.data.TestDataClassConverter::class,
)
abstract class DemoDatabase : RoomDatabase()