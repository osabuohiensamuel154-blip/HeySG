package com.heysg.app

data class Task(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val category: String = "General",
    var isDone: Boolean = false
)
