package com.heysg.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TaskStorage(context: Context) {
    private val prefs = context.getSharedPreferences("heysg_tasks", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun save(tasks: List<Task>) {
        prefs.edit().putString("tasks", gson.toJson(tasks)).apply()
    }

    fun load(): MutableList<Task> {
        val json = prefs.getString("tasks", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Task>>() {}.type
        return gson.fromJson(json, type)
    }
}
