package com.voice.voicerecorder.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [Record::class], version = 1, exportSchema = false)
abstract class RecordDatabase : RoomDatabase() {

    abstract fun dao(): RecordDao

    companion object {
        val DATABASE_NAME = "RECORD_DATABASE"
    }

    class Callback @Inject constructor(
        private val database: Provider<RecordDatabase>,
        private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val dao = database.get().dao()
            applicationScope.launch {
                // you can do pre-populating here
            }
        }
    }
}