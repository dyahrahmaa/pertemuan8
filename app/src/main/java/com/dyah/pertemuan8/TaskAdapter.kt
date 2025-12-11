package com.dyah.pertemuan8

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dyah.pertemuan8.databinding.ItemTaskBinding

class TaskAdapter(
    private var tasks: List<Task>,
    private val listener: Listener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    interface Listener {
        fun onEdit(task: Task)
        fun onDelete(task: Task)
        fun onToggleComplete(task: Task, completed: Boolean)
    }

    inner class TaskViewHolder(private val b: ItemTaskBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(task: Task) {
            b.tvTitle.text = task.title
            b.tvDescription.text = task.description
            b.tvDeadline.text = task.deadline ?: ""
            b.checkboxComplete.isChecked = task.completed

            if (task.completed) {
                b.tvTitle.paintFlags = b.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                b.tvTitle.paintFlags = b.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            b.root.setOnClickListener { listener.onEdit(task) }

            b.btnDelete.setOnClickListener { listener.onDelete(task) }

            b.checkboxComplete.setOnCheckedChangeListener(null)
            b.checkboxComplete.isChecked = task.completed
            b.checkboxComplete.setOnCheckedChangeListener { _, isChecked ->
                listener.onToggleComplete(task, isChecked)
            }
        }
    }

    fun updateList(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val b = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(b)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size
}