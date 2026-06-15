package com.sumeyye.urbanflora.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sumeyye.urbanflora.ui.view.MainScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "main_content",
        modifier = modifier
    ) {
        composable("main_content") {
            MainScreen(navController = navController)
        }
    }
}
