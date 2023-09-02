package com.taha.todo.ui.todo.add_todo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.taha.todo.TodoHelper.list
import com.taha.todo.data.Todo
import com.taha.todo.data.TodoRepository
import com.taha.todo.rules.TestCoroutineRule
import com.taha.todo.ui.add_edit_todo.AddEditTodoEvents
import com.taha.todo.ui.add_edit_todo.AddEditViewModel
import com.taha.todo.util.UiEvents
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditTodoViewModelTest {

    var repository: TodoRepository = mockk<TodoRepository>()

    @get:Rule
    val instantExecuterRule = InstantTaskExecutorRule()

    @get:Rule
    val testDispatcher = TestCoroutineRule(TestCoroutineDispatcher())
    lateinit var addEditViewModel: AddEditViewModel
    val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)

    @Before
    fun Setup() = runTest {
        every { savedStateHandle.get<Int>("todoId") }.answers {
            -1
        }
        coEvery { repository.getTodo(any()) } answers {
            list.first()
        }
        addEditViewModel = AddEditViewModel(repository, savedStateHandle)
    }

    @Test
    fun `test when date changes`() = runTest {
        val time = 1693650150874
        addEditViewModel.onEvent(AddEditTodoEvents.OnDateChange(time))
        Assert.assertEquals(
            "02 09 2023",
            addEditViewModel.date
        )
    }

    @Test
    fun `test when save click and title is empty`() = runTest {
        addEditViewModel.onEvent(AddEditTodoEvents.OnTitleChange(""))

        addEditViewModel.onEvent(AddEditTodoEvents.OnSaveClicked)

        Assert.assertEquals(true, addEditViewModel.titleDescriptionError.isNotBlank())


    }

    @Test
    fun `test when save click and title is not empty and priority is empty`() = runTest {
        addEditViewModel.onEvent(AddEditTodoEvents.OnTitleChange("title"))

        println("value is ${addEditViewModel.priority}")
        addEditViewModel.uiState.test {
            addEditViewModel.onEvent(AddEditTodoEvents.OnSaveClicked)



            Assert.assertThat(awaitItem(), instanceOf(UiEvents.ShowSnackBar::class.java))

        }
    }

    @Test
    fun `test when save click and title is not empty and priority is not empty`() = runTest {
        addEditViewModel.onEvent(AddEditTodoEvents.OnTitleChange("title"))

        addEditViewModel.onEvent(AddEditTodoEvents.OnPriorityChange(0))

        coEvery { repository.insertTodo(any()) } answers {
            list.add(
                Todo(
                    id = 6,
                    title = "test item is medium inserted",
                    date = "12 08 2023",
                    isDone = false,
                    priority = 5,
                    description = null
                ),
            )
        }
        every { repository.getAllTodo() } answers {
            flowOf(
                list
            )
        }
        addEditViewModel.uiState.test {
            addEditViewModel.onEvent(AddEditTodoEvents.OnSaveClicked)
            Assert.assertThat(awaitItem(), instanceOf(UiEvents.PopBackStack::class.java))

        }


    }


    @Test
    fun `test when Priority changed`() = runTest {
        addEditViewModel.onEvent(AddEditTodoEvents.OnPriorityChange(0))

        Assert.assertEquals("Low", addEditViewModel.priority)


    }

}