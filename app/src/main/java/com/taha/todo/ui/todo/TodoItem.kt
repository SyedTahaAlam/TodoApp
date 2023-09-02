package com.taha.todo.ui.todo

import android.annotation.SuppressLint
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taha.todo.data.Todo
import kotlin.math.roundToInt

@Composable
fun TodoItem(
    todo: Todo,
    onEvent: (TodoEvents) -> Unit
) {
    Surface(color = getColorOnPriority(todo.priority)) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 8.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onEvent(TodoEvents.OnTodoClick(todo))
                    }
                    .padding(2.dp),

                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    checkmarkColor = MaterialTheme.colors.secondary,
                    uncheckedColor = Color.Gray
                ), checked = todo.isDone, onCheckedChange = { isCheckChange ->
                    onEvent(TodoEvents.OnDoneChange(todo, isCheckChange))
                })
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = todo.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W500,
                        color = Color.White
                    )


                    todo.description?.let { description ->
                        if (description.isNotBlank()) {
                            Text(
                                text = description,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }

                    if (todo.date.isNotBlank()) {
                        Text(
                            text = todo.date,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }

}

fun getColorOnPriority(priority: Int): Color {
    return when (priority) {
        0 -> Color(0xEAA3DDBA)
        1 -> Color(0xFFE6B06E)
        2 -> Color(0xFFC45959)
        else -> Color.White
    }

}

@Preview
@Composable
fun todo() {
    TodoItem(Todo(title = "text", description = null, isDone = false, date = "", priority = 0)) {

    }
}

const val ANIMATION_DURATION = 250
const val CARD_OFFSET = -56f

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun DraggableCardComplex(
    isRevealed: Boolean = false,
    todo: Todo,
    offset: Float,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    onClick: (TodoEvents) -> Unit
) {
    val offsetX = remember {
        mutableStateOf(0f)
    }
    val transitionState = remember { MutableTransitionState("$isRevealed ${todo.id}") }

    val transition = updateTransition(transitionState, todo.id.toString())

    val offsetTransition by transition.animateFloat(
        label = todo.id.toString(),
        transitionSpec = { tween(durationMillis = ANIMATION_DURATION) },
        targetValueByState = { if (isRevealed) offset + offsetX.value else -offsetX.value },

        )

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        modifier = Modifier
            .wrapContentSize()
            .offset { IntOffset((offsetX.value + offsetTransition).roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->

                    val original = Offset(offsetX.value, 0f)
                    val summed = original + Offset(x = dragAmount, y = 0f)
                    val newValue = Offset(x = summed.x.coerceIn(offset, 0f), y = 0f)
                    println("value is ${newValue.x}")
                    if (newValue.x < 0) {
                        onExpand()
                        return@detectHorizontalDragGestures
                    } else if (newValue.x <= 0) {
                        onCollapse()
                        return@detectHorizontalDragGestures
                    }
                    offsetX.value = newValue.x
                }
            },
        content = { TodoItem(todo = todo, onEvent = onClick) }
    )
}