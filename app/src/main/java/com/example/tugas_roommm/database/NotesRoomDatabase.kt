package com.example.Tugas_Roomm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [Notes::class], version = 1, exportSchema = false)
abstract class NotesRoomDatabase : RoomDatabase(){
    abstract fun notesDao(): NotesDao?

    companion object{
        @Volatile
        private var INSTANCE: NotesRoomDatabase? = null
        fun getDatabase(context: Context): NotesRoomDatabase?{
            if (INSTANCE == null){
                synchronized(NotesRoomDatabase::class.java){
                        INSTANCE = databaseBuilder(
                            context.applicationContext,
                            NotesRoomDatabase::class.java, "NoteDatabase"
                        )
                            .build()
                }
            }
            return INSTANCE
        }
    }
}