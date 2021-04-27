package com.k310.fitness.injection

import android.content.Context
import androidx.room.Room
import com.k310.fitness.other.Constants.TRAINING_DB_NAME
import com.k310.fitness.trainingdb.TrainingDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
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
        TrainingDB::class.java,
        TRAINING_DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideTrainingDAO(db: TrainingDB) = db.getTrainingDao()
}