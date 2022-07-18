package com.dicoding.todoapp.data

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


//TODO 1 : Define a local database table using the schema in app/schema/tasks.json
@Parcelize
@Entity(tableName = "tasks")
data class Task(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

//    @ColumnInfo(name = "title")
    val title: String,

//    @ColumnInfo(name = "description")
    val description: String,

//    @ColumnInfo(name = "dueDate")
    val dueDateMillis: Long,

    @ColumnInfo(name = "completed")
    val isCompleted: Boolean = false
): Parcelable
