package com.heysg.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.heysg.app.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storage: TaskStorage
    private lateinit var adapter: TaskAdapter
    private val tasks = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = TaskStorage(this)
        tasks.addAll(storage.load())

        setupHeader()
        setupList()
        setupFab()
        refreshUi()
    }

    private fun setupHeader() {
        binding.tvDate.text = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
    }

    private fun setupList() {
        adapter = TaskAdapter(
            tasks,
            onDelete = { pos ->
                tasks.removeAt(pos)
                adapter.notifyItemRemoved(pos)
                storage.save(tasks)
                refreshUi()
            },
            onToggle = { pos ->
                tasks[pos] = tasks[pos].copy(isDone = !tasks[pos].isDone)
                adapter.notifyItemChanged(pos)
                storage.save(tasks)
                refreshUi()
            }
        )
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener { showAddDialog() }
    }

    private fun showAddDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val etTitle = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etTitle)
        val etCategory = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etCategory)

        MaterialAlertDialogBuilder(this)
            .setTitle("New Task")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val title = etTitle.text?.toString()?.trim().orEmpty()
                val category = etCategory.text?.toString()?.trim().ifNullOrBlank("General")
                if (title.isNotEmpty()) {
                    tasks.add(0, Task(title = title, category = category))
                    adapter.notifyItemInserted(0)
                    binding.rvTasks.scrollToPosition(0)
                    storage.save(tasks)
                    refreshUi()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()

        etTitle.requestFocus()
    }

    private fun refreshUi() {
        val done = tasks.count { it.isDone }
        val total = tasks.size
        binding.tvProgress.text = "$done / $total done"
        binding.progressBar.progress = if (total > 0) done * 100 / total else 0
        binding.tvEmpty.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        binding.rvTasks.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun String?.ifNullOrBlank(default: String) =
        if (isNullOrBlank()) default else this!!
}
