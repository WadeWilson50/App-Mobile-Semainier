package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        RecipeEntity::class,
        MealSlotEntity::class,
        GroceryCheckEntity::class,
        ManualGroceryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SemainierDatabase : RoomDatabase() {
    abstract fun semainierDao(): SemainierDao

    companion object {
        @Volatile
        private var INSTANCE: SemainierDatabase? = null

        fun getInstance(context: Context): SemainierDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SemainierDatabase::class.java,
                    "semainier_gourmand.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
