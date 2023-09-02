package com.taha.todo.ui.todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.todo.data.Todo
import com.taha.todo.data.TodoRepository
import com.taha.todo.util.Routes
import com.taha.todo.util.UiEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(val repository: TodoRepository) : ViewModel() {
    var state by mutableStateOf(TodoUiState())
        private set

    init {
        viewModelScope.launch {
            repository.getAllTodo().collectLatest {
                state = state.copy(
                    todos = it
                )
            }
        }
    }


    private val _uiState = MutableSharedFlow<UiEvents>()
    val uiState = _uiState.asSharedFlow()


    private var deleteTodo: Todo? = null
    fun onEvent(event: TodoEvents) {
        when (event) {
            is TodoEvents.SortHighToLowPriority -> {
                print("i am here")
                state = state.copy(
                    todos = state.todos.sortedByDescending { it.priority }
                )
            }
            is TodoEvents.OnAddTodoClick -> sendUIEvents(UiEvents.Navigate(Routes.ADD_EDIT_TODO))

            is TodoEvents.OnDeleteTodoClick -> {
                deleteTodo = event.todo
                viewModelScope.launch {
                    repository.deleteTodo(event.todo)
                    sendUIEvents(
                        UiEvents.ShowSnackBar(
                            message = "Todo Deleted Successfully",
                            actions = "Undo"
                        )
                    )
                }

            }

            is TodoEvents.OnDoneChange -> {
                viewModelScope.launch {
                    repository.insertTodo(
                        event.todo.copy(
                            isDone = event.change
                        )
                    )
                }
            }

            is TodoEvents.OnTodoClick -> sendUIEvents(UiEvents.Navigate("${Routes.ADD_EDIT_TODO}?todoId=${event.todo.id}"))

            is TodoEvents.OnUndoTodoClick -> {
                deleteTodo?.let { todo ->
                    viewModelScope.launch {
                        repository.insertTodo(todo)
                    }
                }
            }
            is TodoEvents.SortByDate -> {
                if (event.type == "recent") {
                    state = state.copy(
                        todos = state.todos.sortedByDescending { it.date }
                    )

                } else {
                    state = state.copy(
                        todos = state.todos.sortedBy { it.date }
                    )
                }
            }
            TodoEvents.SortLowToHighPriority -> {
                state = state.copy(
                    todos = state.todos.sortedBy { it.priority }
                )
            }
        }

    }

    private fun sendUIEvents(uiEvents: UiEvents) {
        viewModelScope.launch {
            _uiState.emit(uiEvents)
        }
    }

}