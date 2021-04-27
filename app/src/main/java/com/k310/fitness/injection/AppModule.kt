package com.k310.fitness.injection

import android.content.Context
import androidx.room.Room
import com.k310.fitness.db.AppDatabase
import com.k310.fitness.util.Constants.APP_DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTrainingDB(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        APP_DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideTrainingDAO(db: AppDatabase) = db.trainingDao()

    @Singleton
    @Provides
    fun provideScheduleDAO(db: AppDatabase) = db.scheduleDao()
}