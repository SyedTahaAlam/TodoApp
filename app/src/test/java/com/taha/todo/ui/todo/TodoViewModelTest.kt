package com.taha.todo.ui.todo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.taha.todo.TodoHelper.list
import com.taha.todo.data.Todo
import com.taha.todo.data.TodoRepository
import com.taha.todo.rules.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any

class TodoViewModelTest {

    var repository: TodoRepository = mockk<TodoRepository>()

    @get:Rule
    val instantExecuterRule = InstantTaskExecutorRule()

    @get:Rule
    val testDispatcher = TestCoroutineRule(TestCoroutineDispatcher())

    lateinit var todoViewModel: TodoViewModel

    @Before
    fun Setup() = runTest {
        every { repository.getAllTodo() } answers {
            flowOf(
                list
            )
        }
        todoViewModel = TodoViewModel(repository)
    }

    @Test
    fun `test that the uistate is populated`() {
        Assert.assertEquals(5, todoViewModel.state.todos.size)
    }


    @Test
    fun `test that the uistate is populated and items are placed`() = runBlocking {
        Assert.assertEquals(4, todoViewModel.state.todos.size)
    }


    @Test
    fun `test handle event handle SortHighToLowPriority`() {
        todoViewModel.onEvent(TodoEvents.SortHighToLowPriority)
        Assert.assertEquals(todoViewModel.state.todos[0].title, "test item is high inserted")
        Assert.assertEquals(todoViewModel.state.todos[1].title, "test item is medium inserted")
        Assert.assertEquals(todoViewModel.state.todos.last().title, "test item is low inserted")
    }


    @Test
    fun `test handle event handle SortLowToHighPriority`() {
        todoViewModel.onEvent(TodoEvents.SortLowToHighPriority)
        Assert.assertEquals(todoViewModel.state.todos[0].title, "test item is low inserted")
        Assert.assertEquals(todoViewModel.state.todos[1].title, "test item is medium inserted")
        Assert.assertEquals(todoViewModel.state.todos.last().title, "test item is high inserted")
    }

    @Test
    fun `test handle event handle SortByDate recent`() = runBlocking {
        todoViewModel.onEvent(TodoEvents.SortByDate("recent"))
        print("data ${todoViewModel.state.todos}")
        Assert.assertEquals(todoViewModel.state.todos[0].date, "12 08 2023")
        Assert.assertEquals(todoViewModel.state.todos.last().date, "09 09 2023")
    }

    @Test
    fun `test handle event handle SortByDate older`() = runBlocking {
        todoViewModel.onEvent(TodoEvents.SortByDate("older"))
        print("data ${todoViewModel.state.todos}")
        Assert.assertEquals(todoViewModel.state.todos.last().date, "12 08 2023")
        Assert.assertEquals(todoViewModel.state.todos.first().date, "09 09 2023")
    }

    @Test
    fun `test handle event handle delete Order`() = runBlocking {
        todoViewModel.onEvent(TodoEvents.OnUndoTodoClick)
        list.remove(
            Todo(
                id = 5,
                title = "test item is  medium inserted",
                date = "10 09 2023",
                isDone = false,
                priority = 2,
                description = null
            )
        )
        coEvery {
            repository.deleteTodo(any())
        }.answers {
            any()
        }

        every { repository.getAllTodo() } answers {
            flowOf(
                list
            )
        }
        todoViewModel.repository.getAllTodo()
        Assert.assertEquals(4, todoViewModel.state.todos.size)


    }

    @Test
    fun `test handle event handle Undo Order`() = runBlocking {
        todoViewModel.onEvent(TodoEvents.OnUndoTodoClick)
        list.remove(
            Todo(
                id = 5,
                title = "test item is  medium inserted",
                date = "10 09 2023",
                isDone = false,
                priority = 2,
                description = null
            )
        )
        coEvery {
            repository.deleteTodo(any())
        }.answers {
            any()
        }

        every { repository.getAllTodo() } answers {
            flowOf(
                list
            )
        }
        todoViewModel.repository.getAllTodo()
        Assert.assertEquals(4, todoViewModel.state.todos.size)
        list.add(
            Todo(
                id = 5,
                title = "test item is  medium inserted",
                date = "10 09 2023",
                isDone = false,
                priority = 2,
                description = null
            )
        )
        todoViewModel.repository.getAllTodo()
        Assert.assertEquals(5, todoViewModel.state.todos.size)


    }


}