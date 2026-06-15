package com.sumeyye.urbanflora.ui.navigation

sealed class Screen(val route: String) {
    object Map : Screen("map")
    object Camera : Screen("camera")
    object Library : Screen("library")
    object Profile : Screen("profile")
}
