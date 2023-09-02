package com.taha.todo.ui.todo

import com.taha.todo.data.Todo

sealed class TodoEvents {
    object OnAddTodoClick : TodoEvents()
    data class OnDeleteTodoClick(val todo: Todo) : TodoEvents()
    data class OnTodoClick(val todo: Todo) : TodoEvents()
    data class OnDoneChange(val todo: Todo, val change: Boolean) : TodoEvents()
    object OnUndoTodoClick : TodoEvents()
    object SortHighToLowPriority : TodoEvents()
    object SortLowToHighPriority : TodoEvents()
    data class SortByDate(val type: String) : TodoEvents()
}
