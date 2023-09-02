package com.taha.todo.ui.todo

import com.taha.todo.data.Todo

data class TodoUiState(
    var todos: List<Todo> = emptyList()
)