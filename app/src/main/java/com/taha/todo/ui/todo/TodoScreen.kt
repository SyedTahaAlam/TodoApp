package com.taha.todo.ui.todo

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taha.todo.util.UiEvents
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun TodoScreen(
    todoViewModel: TodoViewModel = hiltViewModel(),
    onNavigate: (UiEvents.Navigate) -> Unit
) {
    val todos = todoViewModel.state
    val scaffoldState = rememberScaffoldState()
    var revealedList = remember { mutableStateListOf<Int>() }

    val isDragged = rememberSaveable {
        false
    }
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    HandleUiEvent(todoViewModel.uiState, todoViewModel, onNavigate, scaffoldState)

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { todoViewModel.onEvent(TodoEvents.OnAddTodoClick) },
                text = { Text(text = "Add") },
                icon = { Icon(Icons.Filled.Add, "") })
        },
        topBar = {
            TopAppBar(
                elevation = 4.dp,
                title = {
                    Text("Todo App")
                },
                backgroundColor = MaterialTheme.colors.primarySurface,

                actions = {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(Icons.Filled.Menu, null)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {

                        DropdownMenuItem(
                            content = {
                                Text("Priority Low -> High")
                            },
                            onClick = {
                                menuExpanded = false
                                println("iam here")
                                todoViewModel.onEvent(TodoEvents.SortLowToHighPriority)
                            },
                        )
                        DropdownMenuItem(
                            content = {
                                Text("Priority High -> Low")
                            },
                            onClick = {
                                todoViewModel.onEvent(
                                    TodoEvents.SortHighToLowPriority
                                )
                            },
                        )
                        DropdownMenuItem(
                            content = {
                                Text("Recent First")
                            },
                            onClick = {
                                todoViewModel.onEvent(
                                    TodoEvents.SortByDate("recent")
                                )
                            },
                        )
                        DropdownMenuItem(
                            content = {
                                Text("Oldest")
                            },
                            onClick = {
                                todoViewModel.onEvent(
                                    TodoEvents.SortByDate("older")
                                )
                            },
                        )
                    }

                })
        }

    ) {
        if (todos.todos.isEmpty()) {

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                itemsIndexed(
                    items = todos.todos,
                    key = { _, item -> run { item.id ?: 0 } }) { index, data ->

                    Box(

                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(
                                color = MaterialTheme.colors.error,
                                shape = RoundedCornerShape(20.dp)
                            )
                    )
                    {


                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .wrapContentSize()


                        ) {


                            AnimatedVisibility(
                                modifier = Modifier
                                    .size(56.dp)
                                    .align(Alignment.Center),
                                enter = fadeIn(initialAlpha = 0.4f),
                                exit = fadeOut(animationSpec = tween(durationMillis = ANIMATION_DURATION)),
                                visible = revealedList.contains(data.id)
                            ) {

                                IconButton(
                                    modifier = Modifier
                                        .size(56.dp),
                                    onClick = {
                                        todoViewModel.onEvent(
                                            TodoEvents.OnDeleteTodoClick(
                                                data
                                            )
                                        )
                                    },
                                    content = {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            tint = Color.White,
                                            contentDescription = "delete action",
                                        )
                                    }
                                )
                            }

                        }
                        DraggableCardComplex(
                            isRevealed = revealedList.contains(data.id),
                            onExpand = {
//                            isDragged = true
                                println("onExpand is called ${data.id}")
                                revealedList.add(data.id ?: 0)
                            },
                            offset = CARD_OFFSET.dp(),
                            onCollapse = {
                                revealedList.remove(data.id ?: 0)

                            },
                            todo = data,
                        ) {
                            todoViewModel.onEvent(it)
                        }

                    }
                }
            }
        }

    }
}

val density: Float
    get() = Resources.getSystem().displayMetrics.density

fun Float.dp(): Float = this * density + 0.5f

@Composable
fun HandleUiEvent(
    uiState: SharedFlow<UiEvents>,
    todoViewModel: TodoViewModel,
    onNavigate: (UiEvents.Navigate) -> Unit,
    scaffoldState: ScaffoldState
) {

    LaunchedEffect(key1 = true) {
        uiState.collectLatest { event ->
            when (event) {
                is UiEvents.Navigate -> onNavigate(event)
                is UiEvents.ShowSnackBar -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actions,
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        todoViewModel.onEvent(TodoEvents.OnUndoTodoClick)
                    }
                }
                else -> Unit
            }

        }
    }
}
