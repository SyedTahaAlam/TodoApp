package com.taha.todo.ui.add_edit_todo

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.taha.todo.util.Constants.priorities
import com.taha.todo.util.UiEvents
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun AddEditScreen(
    onPopBackStack: () -> Unit,
    viewModel: AddEditViewModel = hiltViewModel()
) {

    val scaffoldState = rememberScaffoldState()

    HandleUiEvent(viewModel.uiState, onPopBackStack, scaffoldState)
    val focusManager = LocalFocusManager.current

    val calendar = Calendar.getInstance()

    // set the initial date
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var selectedDate by remember {
        mutableLongStateOf(calendar.timeInMillis)
    }

    var mExpanded by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                selectedDayContainerColor = MaterialTheme.colors.primaryVariant

            ),
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    viewModel.onEvent(AddEditTodoEvents.OnDateChange(datePickerState.selectedDateMillis!!))
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                elevation = 4.dp,
                title = {
                    Text("Add Todo")
                },
                backgroundColor = MaterialTheme.colors.primarySurface,
                navigationIcon = {
                    IconButton(onClick = { onPopBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                },
            )
        }

    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {


            Column(
                Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                OutlinedTextField(
                    value = viewModel.title,
                    onValueChange = {
                        viewModel.onEvent(AddEditTodoEvents.OnTitleChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Title") },
                    maxLines = 1,
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    isError = viewModel.titleDescriptionError.isNotBlank()
                )
                if (viewModel.titleDescriptionError.isNotBlank())
                    Text(
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 16.dp),
                        text = viewModel.titleDescriptionError
                    )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.description,
                    onValueChange = {
                        viewModel.onEvent(AddEditTodoEvents.OnDescriptionChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Description(Optional)") },
                    maxLines = 5,
                    singleLine = false,

                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = viewModel.date,
                    onValueChange = {
                    },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDatePicker = true
                        },
                    label = { Text(text = "Date") },
                    maxLines = 1,
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    OutlinedTextField(
                        value = viewModel.priority,
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { mExpanded = !mExpanded },
                        label = { Text("Priority") },
                        enabled = false,
                        maxLines = 1,
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                if (mExpanded)
                                    Icons.Filled.KeyboardArrowUp
                                else
                                    Icons.Filled.KeyboardArrowDown,
                                "contentDescription",
                            )
                        }
                    )


                    Box(Modifier.padding(horizontal = 16.dp)) {
                        DropdownMenu(
                            expanded = mExpanded,
                            onDismissRequest = { mExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            priorities.forEach { label ->
                                DropdownMenuItem(onClick = {
                                    viewModel.onEvent(AddEditTodoEvents.OnPriorityChange(label.value))
                                    mExpanded = false
                                }) {
                                    Text(text = label.key)
                                }
                            }
                        }
                    }
                }


            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(4.dp),
                onClick = {
                    viewModel.onEvent(AddEditTodoEvents.OnSaveClicked)
                },
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 2.dp
                ),
            ) {
                Text(text = "Save", fontSize = 14.sp)
            }
        }
    }


}


@Composable
fun HandleUiEvent(
    uiState: SharedFlow<UiEvents>,
    onNavigate: () -> Unit,
    scaffoldState: ScaffoldState
) {

    LaunchedEffect(key1 = true) {
        uiState.collectLatest { event ->
            when (event) {
                is UiEvents.PopBackStack -> onNavigate()
                is UiEvents.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actions,
                        duration = SnackbarDuration.Long
                    )

                }
                else -> Unit
            }

        }
    }
}