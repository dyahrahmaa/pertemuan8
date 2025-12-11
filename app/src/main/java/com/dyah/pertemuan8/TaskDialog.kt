package com.dyah.pertemuan8

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import com.dyah.pertemuan8.databinding.DialogTaskBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskDialog(
    private val context: Context,
    private val task: Task?,
    private val onSave: (Task) -> Unit
) {
    fun show() {
        val binding = DialogTaskBinding.inflate(android.view.LayoutInflater.from(context))

        task?.let {
            binding.editTextTitle.setText(it.title)
            binding.editTextDescription.setText(it.description)
            binding.editTextDeadline.setText(it.deadline)
        }

        binding.editTextDeadline.setOnClickListener {
            val cal = Calendar.getInstance()
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)

            val dp = DatePickerDialog(context, { _, y, m, d ->
                val sel = Calendar.getInstance()
                sel.set(y, m, d)
                val fmt = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                binding.editTextDeadline.setText(fmt.format(sel.time))
            }, year, month, day)
            dp.show()
        }

        val titleDialog = if (task == null) "Tambah Tugas Baru" else "Edit Tugas"
        MaterialAlertDialogBuilder(context)
            .setTitle(titleDialog)
            .setView(binding.root)
            .setPositiveButton("Simpan") { dialog, _ ->
                val title = binding.editTextTitle.text.toString().trim()
                val desc = binding.editTextDescription.text.toString().trim()
                val deadline = binding.editTextDeadline.text.toString().trim()

                if (title.isEmpty()) {
                    Toast.makeText(context, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newTask = if (task == null) {
                    Task(
                        id = null,
                        title = title,
                        description = desc,
                        deadline = deadline,
                        completed = false
                    )
                } else {
                    task.title = title
                    task.description = desc
                    task.deadline = deadline
                    task
                }

                onSave(newTask)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { d, _ -> d.dismiss() }
            .show()
    }
}