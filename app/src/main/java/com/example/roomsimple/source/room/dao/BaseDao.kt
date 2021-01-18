package com.example.roomsimple.source.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(data: T): Int

    @Delete
    fun deleteStudent(data: T): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: T): Long


}