package com.dyah.pertemuan8

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dyah.pertemuan8.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), TaskAdapter.Listener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: TaskAdapter
    private var tasksList = listOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(tasksList, this)
        binding.rvTasks.adapter = adapter

        database = FirebaseDatabase.getInstance().getReference("tasks")

        fetchData()

        binding.fabAddTask.setOnClickListener {
            val dialog = TaskDialog(this, null) { newTask ->
                val id = database.push().key
                if (id != null) {
                    newTask.id = id
                    database.child(id).setValue(newTask)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Tugas disimpan", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Gagal membuat id", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }
    }

    private fun fetchData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Task>()
                for (child in snapshot.children) {
                    val t = child.getValue(Task::class.java)
                    t?.id = child.key
                    t?.let { list.add(it) }
                }

                list.sortWith(compareBy({ it.completed }, { it.deadline }))
                tasksList = list
                adapter.updateList(tasksList)

                if (tasksList.isEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.rvTasks.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvTasks.visibility = View.VISIBLE
                }
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onEdit(task: Task) {
        val dialog = TaskDialog(this, task) { updatedTask ->
            val id = updatedTask.id ?: return@TaskDialog
            database.child(id).setValue(updatedTask)
                .addOnSuccessListener {
                    Toast.makeText(this, "Tugas diperbaharui", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        dialog.show()
    }

    override fun onDelete(task: Task) {
        val id = task.id ?: return
        database.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Tugas dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal hapus: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onToggleComplete(task: Task, completed: Boolean) {
        val id = task.id ?: return
        val updates = mapOf<String, Any>("completed" to completed)
        database.child(id).updateChildren(updates)
    }
}