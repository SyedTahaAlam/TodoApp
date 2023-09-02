package com.taha.todo

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.taha.todo.ui.add_edit_todo.AddEditScreen
import com.taha.todo.ui.todo.TodoScreen
import com.taha.todo.util.Routes

@Composable
fun Navigation(
    navController: NavHostController
) {

    NavHost(navController = navController, startDestination = Routes.TODO_LIST) {
        composable(Routes.TODO_LIST) {
            TodoScreen(
                onNavigate = {
                    navController.navigate(it.route)
                }
            )
        }

        composable("${Routes.ADD_EDIT_TODO}?todoId={todoId}",
            arguments = listOf(
                navArgument(
                    name = "todoId"
                ) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            AddEditScreen(onPopBackStack = { navController.popBackStack() })
        }
    }
}