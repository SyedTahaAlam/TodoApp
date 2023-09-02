package com.taha.todo.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.taha.todo.data.TODODataBase
import com.taha.todo.data.Todo
import com.taha.todo.data.TodoDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*

class TodoDaoTest {

    lateinit var todoDataBase: TODODataBase
    lateinit var todoDao: TodoDao

    @get:Rule
    val instantExecuterRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        todoDataBase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TODODataBase::class.java
        ).allowMainThreadQueries().build()
        todoDao = todoDataBase.dao
    }

    @Test
    fun test_item_is_inserted_in_database() = runBlocking {
        val todo = Todo(
            title = "test item is inserted",
            date = "09 09 2023",
            isDone = false,
            priority = 0,
            description = null
        )

        todoDao.insertTodo(todo)

        val result = todoDao.getAllTodo().first()

        Assert.assertEquals(1, result.size)
        Assert.assertEquals("test item is inserted", result.get(0).title)

    }

    @Test
    fun test_deleted_successfully() = runBlocking {
        val todo = Todo(
            title = "test item is inserted",
            date = "09 09 2023",
            isDone = false,
            priority = 0,
            description = null
        )

        todoDao.insertTodo(todo)

        val data = todoDao.getAllTodo().first()

        todoDao.deleteTodo(data[0])

        val result = todoDao.getAllTodo().first()

        Assert.assertEquals(0, result.size)

    }

    @Test
    fun test_get_todo() = runBlocking {
        val todo = Todo(
            id = 1,
            title = "test item is inserted",
            date = "09 09 2023",
            isDone = false,
            priority = 0,
            description = null
        )
        val todo2 = Todo(
            id = 2,
            title = "test item is second inserted",
            date = "09 09 2023",
            isDone = false,
            priority = 0,
            description = null
        )

        todoDao.insertTodo(todo)
        todoDao.insertTodo(todo2)

        val data = todoDao.getTodo(id = 2)

        Assert.assertEquals("test item is second inserted", data!!.title)

    }

    @After
    fun tearDown() {
        todoDataBase.close()
    }
}