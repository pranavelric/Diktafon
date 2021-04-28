package com.voice.voicerecorder.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(record: Record)

    @Query("SELECT * FROM records")
    fun getAllRecords(): Flow<List<Record>>

    @Query(value = "SELECT * FROM records WHERE title = :title")
    suspend fun getRecord(title: String): Record

    @Query("DELETE FROM records WHERE id = :id")
    suspend fun delete(id: Int)




}