package com.taha.todo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("SELECT * FROM TODO WHERE id = :id")
    suspend fun getTodo(id: Int): Todo?

    @Query("SELECT * FROM TODO")
    fun getAllTodo(): Flow<List<Todo>>

    @Query("SELECT * FROM TODO ORDER BY PRIORITY DESC")
    fun getSortedOnPriorityTodo(): Flow<List<Todo>>

}