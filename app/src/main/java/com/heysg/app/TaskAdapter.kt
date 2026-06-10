package com.heysg.app

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.heysg.app.databinding.ItemTaskBinding

class TaskAdapter(
    val tasks: MutableList<Task>,
    private val onToggle: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        val ctx = holder.binding.root.context
        with(holder.binding) {
            tvTitle.text = task.title
            tvCategory.text = task.category
            cbDone.isChecked = task.isDone

            val (stripColor, priorityLabel, priorityColor) = when (task.priority) {
                Task.Priority.HIGH   -> Triple(R.color.priority_high,   "↑ High",   R.color.priority_high)
                Task.Priority.MEDIUM -> Triple(R.color.priority_medium, "→ Med",    R.color.priority_medium)
                Task.Priority.LOW    -> Triple(R.color.priority_low,    "↓ Low",    R.color.priority_low)
            }
            priorityStrip.setBackgroundColor(ContextCompat.getColor(ctx, stripColor))
            tvPriority.text = priorityLabel
            tvPriority.setTextColor(ContextCompat.getColor(ctx, priorityColor))

            applyDoneStyle(this, task.isDone)
            cbDone.setOnClickListener {
                bounceCard(this)
                onToggle(holder.adapterPosition)
            }
        }
    }

    private fun applyDoneStyle(b: ItemTaskBinding, done: Boolean) {
        if (done) {
            b.tvTitle.paintFlags = b.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            b.tvTitle.alpha = 0.4f
            b.tvCategory.alpha = 0.4f
            b.tvPriority.alpha = 0.4f
        } else {
            b.tvTitle.paintFlags = b.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            b.tvTitle.alpha = 1f
            b.tvCategory.alpha = 1f
            b.tvPriority.alpha = 1f
        }
    }

    private fun bounceCard(b: ItemTaskBinding) {
        b.root.animate().scaleX(0.96f).scaleY(0.96f).setDuration(80)
            .withEndAction { b.root.animate().scaleX(1f).scaleY(1f).setDuration(80).start() }
            .start()
    }

    override fun getItemCount() = tasks.size
}
