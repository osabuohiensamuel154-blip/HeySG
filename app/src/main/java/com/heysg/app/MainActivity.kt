package com.heysg.app

import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.heysg.app.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storage: TaskStorage
    private lateinit var adapter: TaskAdapter
    private val tasks = mutableListOf<Task>()

    private val quotes = arrayOf(
        "One task at a time builds mountains 🏔️",
        "Progress, not perfection 💪",
        "Small steps, big dreams ✨",
        "Today is your day to shine ☀️",
        "Check it off, feel the win 🏆",
        "You've got this, Singapore! 🇸🇬",
        "Make today count 🚀",
        "Steady lah, one thing at a time! 😄",
        "Can one! Just start 💫",
        "Every task done is a victory 🎯",
        "Rise and grind, then rest and shine 🌟",
        "Be the person your future self thanks 🙏"
    )

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) NotificationHelper.scheduleDailyReminder(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = TaskStorage(this)
        tasks.addAll(storage.load())

        NotificationHelper.createChannel(this)
        setupNotificationPermission()
        setupHeader()
        setupList()
        setupFab()
        setupSwipeGestures()
        refreshUi()
    }

    private fun setupNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.scheduleDailyReminder(this)
            } else {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            NotificationHelper.scheduleDailyReminder(this)
        }
    }

    private fun setupHeader() {
        binding.tvDate.text = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        binding.tvQuote.text = quotes[dayOfYear % quotes.size]
    }

    private fun setupList() {
        adapter = TaskAdapter(tasks) { pos ->
            vibrate()
            tasks[pos] = tasks[pos].copy(isDone = !tasks[pos].isDone)
            adapter.notifyItemChanged(pos)
            storage.save(tasks)
            refreshUi()
            if (tasks.isNotEmpty() && tasks.all { it.isDone }) celebrateAllDone()
        }
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter
        binding.rvTasks.layoutAnimation =
            AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down)
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            val sheet = AddTaskBottomSheet()
            sheet.onTaskAdded = { task ->
                tasks.add(0, task)
                adapter.notifyItemInserted(0)
                binding.rvTasks.scrollToPosition(0)
                storage.save(tasks)
                refreshUi()
            }
            sheet.show(supportFragmentManager, "AddTask")
        }
    }

    private fun setupSwipeGestures() {
        val deleteColor = ColorDrawable(Color.parseColor("#D32F2F"))
        val doneColor = ColorDrawable(Color.parseColor("#2E7D32"))
        val deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white)!!
        val doneIcon = ContextCompat.getDrawable(this, R.drawable.ic_check_white)!!

        val callback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    val removed = tasks.removeAt(pos)
                    adapter.notifyItemRemoved(pos)
                    storage.save(tasks)
                    refreshUi()
                    Snackbar.make(binding.root, "Task deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo") {
                            tasks.add(pos, removed)
                            adapter.notifyItemInserted(pos)
                            storage.save(tasks)
                            refreshUi()
                        }
                        .show()
                } else {
                    vibrate()
                    tasks[pos] = tasks[pos].copy(isDone = !tasks[pos].isDone)
                    adapter.notifyItemChanged(pos)
                    storage.save(tasks)
                    refreshUi()
                    if (tasks.all { it.isDone }) celebrateAllDone()
                }
            }

            override fun onChildDraw(
                c: Canvas, rv: RecyclerView, vh: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isActive: Boolean
            ) {
                val item = vh.itemView
                val h = item.bottom - item.top
                if (dX < 0) {
                    deleteColor.setBounds(item.right + dX.toInt(), item.top, item.right, item.bottom)
                    deleteColor.draw(c)
                    val margin = (h - deleteIcon.intrinsicHeight) / 2
                    deleteIcon.setBounds(
                        item.right - margin - deleteIcon.intrinsicWidth, item.top + margin,
                        item.right - margin, item.bottom - margin
                    )
                    deleteIcon.draw(c)
                } else if (dX > 0) {
                    doneColor.setBounds(item.left, item.top, item.left + dX.toInt(), item.bottom)
                    doneColor.draw(c)
                    val margin = (h - doneIcon.intrinsicHeight) / 2
                    doneIcon.setBounds(
                        item.left + margin, item.top + margin,
                        item.left + margin + doneIcon.intrinsicWidth, item.bottom - margin
                    )
                    doneIcon.draw(c)
                }
                super.onChildDraw(c, rv, vh, dX, dY, actionState, isActive)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.rvTasks)
    }

    private fun refreshUi() {
        val done = tasks.count { it.isDone }
        val total = tasks.size
        val pct = if (total > 0) done * 100 / total else 0

        binding.tvStats.text = if (total == 0) "No tasks yet — add one!" else "$done / $total tasks done"
        binding.circularProgress.setProgressCompat(pct, true)
        binding.tvPercent.text = "$pct%"

        binding.emptyState.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        binding.rvTasks.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun celebrateAllDone() {
        binding.circularProgress.animate().scaleX(1.25f).scaleY(1.25f).setDuration(200)
            .withEndAction { binding.circularProgress.animate().scaleX(1f).scaleY(1f).setDuration(200).start() }
            .start()

        val messages = listOf(
            "🎉 All done! You're unstoppable!",
            "🏆 Tasks crushed! Amazing work!",
            "🚀 100%! Singapore proud of you!",
            "✨ Everything done! Legend status!"
        )
        Snackbar.make(binding.root, messages.random(), Snackbar.LENGTH_LONG)
            .setBackgroundTint(Color.parseColor("#2E7D32"))
            .setTextColor(Color.WHITE)
            .show()
    }

    private fun vibrate() {
        val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(45)
        }
    }
}
