package com.voice.voicerecorder.di

import android.content.Context
import androidx.room.Room
import com.voice.voicerecorder.data.RecordDao
import com.voice.voicerecorder.data.RecordDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun providesRecordDatabase(@ApplicationContext context: Context): RecordDatabase {
        return Room.databaseBuilder(
            context,
            RecordDatabase::class.java,
            RecordDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }


    @Singleton
    @Provides
    fun providesRecordDao(recordDatabase: RecordDatabase): RecordDao {
        return recordDatabase.dao()
    }


}