package com.heysg.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.heysg.app.databinding.BottomSheetAddTaskBinding

class AddTaskBottomSheet : BottomSheetDialogFragment() {

    var onTaskAdded: ((Task) -> Unit)? = null
    private var _binding: BottomSheetAddTaskBinding? = null
    private val binding get() = _binding!!
    private var selectedPriority = Task.Priority.MEDIUM
    private var selectedCategory = "General"

    private val categories = listOf(
        "General", "Work 💼", "Health 🏃", "Home 🏠", "Finance 💰", "Shopping 🛒", "Study 📚"
    )
    private val quickTemplates = listOf(
        "💧 Drink 8 glasses of water",
        "🏃 Exercise 30 mins",
        "📚 Read for 30 mins",
        "📱 Reply to messages",
        "🛒 Buy groceries",
        "🍱 Meal prep",
        "😴 Sleep by 11 PM",
        "📞 Call family"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategoryChips()
        setupPriorityChips()
        setupQuickTemplates()

        binding.btnAdd.setOnClickListener {
            val title = binding.etTitle.text?.toString()?.trim().orEmpty()
            if (title.isNotEmpty()) {
                binding.tilTitle.error = null
                onTaskAdded?.invoke(Task(title = title, category = selectedCategory, priority = selectedPriority))
                dismiss()
            } else {
                binding.tilTitle.error = "Enter a task first"
                binding.etTitle.requestFocus()
            }
        }
        binding.etTitle.requestFocus()
    }

    private fun setupCategoryChips() {
        categories.forEachIndexed { i, cat ->
            val chip = Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                isChecked = i == 0
                setOnCheckedChangeListener { _, checked -> if (checked) selectedCategory = cat }
            }
            binding.chipGroupCategory.addView(chip)
        }
    }

    private fun setupPriorityChips() {
        binding.chipLow.setOnCheckedChangeListener { _, c -> if (c) selectedPriority = Task.Priority.LOW }
        binding.chipMedium.setOnCheckedChangeListener { _, c -> if (c) selectedPriority = Task.Priority.MEDIUM }
        binding.chipHigh.setOnCheckedChangeListener { _, c -> if (c) selectedPriority = Task.Priority.HIGH }
        binding.chipMedium.isChecked = true
    }

    private fun setupQuickTemplates() {
        quickTemplates.forEach { template ->
            val chip = Chip(requireContext()).apply {
                text = template
                isClickable = true
                isCheckable = false
                setOnClickListener {
                    binding.etTitle.setText(template)
                    binding.etTitle.setSelection(template.length)
                }
            }
            binding.chipGroupTemplates.addView(chip)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
