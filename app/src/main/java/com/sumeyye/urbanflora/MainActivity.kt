package com.sumeyye.urbanflora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.sumeyye.urbanflora.ui.navigation.NavGraph
import com.sumeyye.urbanflora.ui.theme.UrbanFloraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UrbanFloraTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}