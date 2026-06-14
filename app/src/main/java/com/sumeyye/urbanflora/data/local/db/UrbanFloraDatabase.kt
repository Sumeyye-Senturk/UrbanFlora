package com.sumeyye.urbanflora.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sumeyye.urbanflora.data.local.converters.Converters
import com.sumeyye.urbanflora.data.local.dao.PlantDao
import com.sumeyye.urbanflora.data.local.dao.UserProgressDao
import com.sumeyye.urbanflora.data.local.entity.PlantEntity
import com.sumeyye.urbanflora.data.local.entity.UserProgressEntity

@Database(entities = [PlantEntity::class, UserProgressEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class UrbanFloraDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun userProgressDao(): UserProgressDao

    companion object {
        @Volatile
        private var INSTANCE: UrbanFloraDatabase? = null

        fun getDatabase(context: Context): UrbanFloraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UrbanFloraDatabase::class.java,
                    "urbanflora_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
