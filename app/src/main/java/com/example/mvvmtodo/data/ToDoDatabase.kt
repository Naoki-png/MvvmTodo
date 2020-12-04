package com.example.mvvmtodo.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mvvmtodo.data.models.ToDoData


@Database(entities = [ToDoData::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun toDoDao(): ToDoDao

    companion object {
        //@Volatile means changes to this field is immediately reflected to other thread.
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        fun getDatabase(context: Context): ToDoDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            //databaseインスタンスは一つだけにしておきたいため、synchronizedで一応別スレッドで実行されないようにしておく
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        ToDoDatabase::class.java,
                        "todo_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}