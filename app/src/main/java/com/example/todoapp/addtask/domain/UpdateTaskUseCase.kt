package com.example.todoapp.addtask.domain

import com.example.todoapp.addtask.data.TaskRepository
import com.example.todoapp.addtask.ui.model.TaskModel
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {
    suspend operator fun invoke(taskModel: TaskModel) {
        taskRepository.update(taskModel)
    }
}