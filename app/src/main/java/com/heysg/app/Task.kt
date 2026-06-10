package com.heysg.app

data class Task(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val category: String = "General",
    val priority: Priority = Priority.MEDIUM,
    var isDone: Boolean = false
) {
    enum class Priority { LOW, MEDIUM, HIGH }
}
