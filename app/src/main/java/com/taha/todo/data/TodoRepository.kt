package com.taha.todo.data

import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodo(todo: Todo)

    suspend fun deleteTodo(todo: Todo)

    suspend fun getTodo(id: Int): Todo?

    fun getAllTodo(): Flow<List<Todo>>

    fun getSortedOnPriorityTodo(): Flow<List<Todo>>
}