package com.example.todoapp.addtask.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.addtask.domain.AddTaskUseCase
import com.example.todoapp.addtask.domain.GetTasksUseCase
import com.example.todoapp.addtask.ui.TaskUiState.Error
import com.example.todoapp.addtask.ui.TaskUiState.Loading
import com.example.todoapp.addtask.ui.TaskUiState.Success
import com.example.todoapp.addtask.ui.model.TaskModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    getTasksUseCase: GetTasksUseCase
) : ViewModel() {

    val uiState: StateFlow<TaskUiState> =
        getTasksUseCase().map(::Success)
            .catch { Error(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _tasks = mutableStateListOf<TaskModel>()
    val tasks: List<TaskModel> = _tasks

    fun onDialogClose() {
        _showDialog.value = false
    }

    fun onTaskCreated(task: String) {
        _showDialog.value = false
        _tasks.add(TaskModel(task = task))

        viewModelScope.launch {
            addTaskUseCase(TaskModel(task = task))
        }
    }

    fun onShowDialogClick() {
        _showDialog.value = true
    }

    fun onCheckBoxSelected(taskModel: TaskModel) {
        val index = _tasks.indexOf(taskModel)
        _tasks[index] = _tasks[index].let { task ->
            task.copy(selected = !task.selected)
        }
    }

    fun onItemRemove(taskModel: TaskModel) {
        val task = _tasks.find { task -> task.id == taskModel.id }
        _tasks.remove(task)
    }

}

