package com.taha.todo

import com.taha.todo.data.Todo

object TodoHelper {
    val list = mutableListOf<Todo>(
        Todo(
            id = 1,
            title = "test item is medium inserted",
            date = "12 08 2023",
            isDone = false,
            priority = 2,
            description = null
        ),
        Todo(
            id = 2,
            title = "test item is medium 2 inserted",
            date = "10 09 2023",
            isDone = false,
            priority = 2,
            description = null
        ),
        Todo(
            id = 3,
            title = "test item is low inserted",
            date = "09 09 2023",
            isDone = false,
            priority = 0,
            description = null
        ),
        Todo(
            id = 4,
            title = "test item is high inserted",
            date = "10 08 2023",
            isDone = false,
            priority = 3,
            description = null
        ),
        Todo(
            id = 5,
            title = "test item is  medium inserted",
            date = "10 09 2023",
            isDone = false,
            priority = 2,
            description = null
        )
    )
}