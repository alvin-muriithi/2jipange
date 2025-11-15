package com.strathmore.groupworkmanager.data.database

import androidx.room.TypeConverter
import com.strathmore.groupworkmanager.data.model.TaskPriority
import com.strathmore.groupworkmanager.data.model.TaskStatus

/**
 * Room type converters for converting between complex types and primitives.
 */
class Converters {
    @TypeConverter
    fun fromTaskPriority(value: TaskPriority): String = value.name

    @TypeConverter
    fun toTaskPriority(value: String): TaskPriority = TaskPriority.valueOf(value)

    @TypeConverter
    fun fromTaskStatus(value: TaskStatus): String = value.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    @TypeConverter
    fun fromTimestamp(value: Long?): Long? = value

}