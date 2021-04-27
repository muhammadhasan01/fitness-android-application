/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.k310.fitness.db


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.k310.fitness.db.converters.BitmapConverter
import com.k310.fitness.db.converters.CalendarConverter
import com.k310.fitness.db.schedule.Schedule
import com.k310.fitness.db.schedule.ScheduleDAO
import com.k310.fitness.db.training.Training
import com.k310.fitness.db.training.TrainingDAO

/**
 * The Room database that contains the Data table
 */
@Database(entities = [Schedule::class, Training::class], version = 1)
@TypeConverters(CalendarConverter::class, BitmapConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDAO
    abstract fun trainingDao(): TrainingDAO

}

