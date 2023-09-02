package com.taha.todo.data

import kotlinx.coroutines.flow.Flow

class TodoRepositoryImpl(private val dao: TodoDao) : TodoRepository {
    override suspend fun insertTodo(todo: Todo) {
        dao.insertTodo(todo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        dao.deleteTodo(todo)
    }

    override suspend fun getTodo(id: Int): Todo? {
        return dao.getTodo(id)
    }

    override fun getAllTodo(): Flow<List<Todo>> {
        return dao.getAllTodo()
    }

    override fun getSortedOnPriorityTodo(): Flow<List<Todo>> {
        return dao.getSortedOnPriorityTodo()
    }
}