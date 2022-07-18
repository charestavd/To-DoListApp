package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.databinding.ActivityTaskDetailBinding
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var viewModel : DetailTaskViewModel
    private lateinit var binding : ActivityTaskDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO 11 : Show detail task and implement delete action
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory).get(DetailTaskViewModel::class.java)

        val taskId: Int?
        if (intent.hasExtra(TASK_ID)){
            taskId = intent.getIntExtra(TASK_ID, -1)
            val task = viewModel.getTaskById(taskId = taskId)
            task.observe(this) {
                if (it == null) {
                    finish()
                } else {
                    bind(task = it)
                }
            }
        }
    }

    private fun bind(task: Task){
        binding.detailEdTitle.setText(task.title)
        binding.detailEdDueDate.setText(DateConverter.convertMillisToString(task.dueDateMillis))
        binding.detailEdDescription.setText(task.description)

        binding.btnDeleteTask.setOnClickListener {
            viewModel.deleteTask(task)
            finish()
            Toast.makeText(applicationContext, "Task has been deleted", Toast.LENGTH_SHORT).show()
        }
    }
}