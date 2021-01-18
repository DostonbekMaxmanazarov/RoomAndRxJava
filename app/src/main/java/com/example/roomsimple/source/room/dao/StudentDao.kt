package com.example.roomsimple.source.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.roomsimple.source.room.entity.StudentData

@Dao
interface StudentDao :BaseDao<StudentData>{

    @Query("SELECT * FROM StudentData")
    fun loadStudents(): LiveData<MutableList<StudentData>>

    @Query("select * from StudentData where id=:newStudentId")
    fun loadStudentById(newStudentId: Long): LiveData<StudentData?>
}