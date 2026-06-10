package com.heysg.app

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heysg.app.databinding.ItemTaskBinding

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onDelete: (Int) -> Unit,
    private val onToggle: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        with(holder.binding) {
            tvTitle.text = task.title
            tvCategory.text = task.category
            cbDone.isChecked = task.isDone
            applyDoneStyle(this, task.isDone)

            cbDone.setOnClickListener { onToggle(holder.adapterPosition) }
            btnDelete.setOnClickListener { onDelete(holder.adapterPosition) }
        }
    }

    private fun applyDoneStyle(b: ItemTaskBinding, done: Boolean) {
        b.tvTitle.paintFlags = if (done)
            b.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            b.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        b.tvTitle.alpha = if (done) 0.45f else 1.0f
    }

    override fun getItemCount() = tasks.size
}
