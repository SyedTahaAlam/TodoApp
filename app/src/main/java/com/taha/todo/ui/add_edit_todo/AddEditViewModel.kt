package com.taha.todo.ui.add_edit_todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.todo.data.Todo
import com.taha.todo.data.TodoRepository
import com.taha.todo.util.UiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: TodoRepository,
    saveStateHiltViewModel: SavedStateHandle
) : ViewModel() {

    var todo by mutableStateOf<Todo?>(null)
        private set

    var titleDescriptionError by mutableStateOf("")
        private set

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var date by mutableStateOf("")
        private set

    var priority by mutableStateOf<String>("")
        private set

    private val _uiState = MutableSharedFlow<UiEvents>()
    val uiState = _uiState.asSharedFlow()

    init {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("dd MM yyyy", Locale.ROOT)
        date = formatter.format(calendar.timeInMillis)

        val todoId = saveStateHiltViewModel.get<Int>("todoId")
        if (todoId != -1) {
            viewModelScope.launch {
                repository.getTodo(todoId!!)?.let { todo ->
                    title = todo.title
                    description = todo.description ?: ""
                    date = todo.date
                    priority = getPriorityString(todo.priority)
                    this@AddEditViewModel.todo = todo
                }
            }
        }
    }

    private fun getPriorityString(priority: Int): String {
        return when (priority) {
            0 -> "Low"
            1 -> "Medium"
            2 -> "High"
            else -> ""
        }
    }

    private fun getPriorityValue(priority: String): Int {
        return when (priority) {
            "Low" -> 0
            "Medium" -> 1
            "High" -> 2
            else -> -1
        }
    }

    /**
     * function that handles the actions from ui to viewmodel
     */
    fun onEvent(events: AddEditTodoEvents) {
        when (events) {
            is AddEditTodoEvents.OnDateChange -> {
                val formatter = SimpleDateFormat("dd MM yyyy", Locale.ROOT)
                date = formatter.format(events.date)
            }
            is AddEditTodoEvents.OnDescriptionChange -> {
                description = events.description
            }
            AddEditTodoEvents.OnSaveClicked -> {
                if (title.isBlank()) {
                    titleDescriptionError = "Title Can Not Be Empty"
                    sendUIEvents(
                        UiEvents.ShowSnackBar(
                            "Title Can Not Be Empty",
                            null
                        )
                    )
                    return
                }
                if (priority.isBlank()) {

                    sendUIEvents(
                        UiEvents.ShowSnackBar(
                            "Select Priority",
                            null
                        )
                    )
                    return
                }
                val todo = Todo(
                    id = todo?.id,
                    date = date,
                    description = description,
                    title = title,
                    isDone = this@AddEditViewModel.todo?.isDone ?: false,
                    priority = getPriorityValue(priority)
                )
                viewModelScope.launch {
                    repository.insertTodo(todo)
                    sendUIEvents(UiEvents.PopBackStack)
                }
            }
            is AddEditTodoEvents.OnTitleChange -> {
                title = events.title
                titleDescriptionError = ""
            }

            is AddEditTodoEvents.OnPriorityChange -> {
                priority = getPriorityString(events.priority)
            }
        }
    }

    private fun sendUIEvents(uiEvents: UiEvents) {
        viewModelScope.launch {
            _uiState.emit(uiEvents)
        }
    }

}