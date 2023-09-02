package com.taha.todo.ui.add_edit_todo

sealed class AddEditTodoEvents {
    data class OnTitleChange(val title: String) : AddEditTodoEvents()
    data class OnDescriptionChange(val description: String) : AddEditTodoEvents()
    data class OnDateChange(val date: Long) : AddEditTodoEvents()
    data class OnPriorityChange(val priority: Int) : AddEditTodoEvents()
    object OnSaveClicked : AddEditTodoEvents()
}
