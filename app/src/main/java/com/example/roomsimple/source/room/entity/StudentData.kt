package com.example.roomsimple.source.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StudentData(
    val name: String = "",
    val age: Int = 16,
    val email: String = "",
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)