package com.sumeyye.urbanflora.ui.view

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sumeyye.urbanflora.domain.model.UserProgress
import com.sumeyye.urbanflora.ui.navigation.Screen
import com.sumeyye.urbanflora.ui.viewmodel.ProfileViewModel

@Composable
fun MainScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Map.route

    val userProgressState by profileViewModel.userProgress.collectAsState()
    val progress = userProgressState ?: UserProgress()

    Scaffold(
        topBar = {
            UrbanTopBar(
                progress = progress,
                onProfileClick = {
                    nestedNavController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Map.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        bottomBar = {
            UrbanBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    nestedNavController.navigate(route) {
                        popUpTo(Screen.Map.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = nestedNavController,
                startDestination = Screen.Map.route
            ) {
                composable(Screen.Map.route) {
                    MapScreen(navController = navController)
                }
                composable(Screen.Camera.route) {
                    CameraScreen(navController = navController)
                }
                composable(Screen.Library.route) {
                    DiscoveryLibraryScreen()
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(navController = navController)
                }
            }
        }
    }
}

@Composable
fun UrbanTopBar(
    progress: UserProgress,
    onProfileClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        color = Color.Transparent,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "UrbanFlora",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Doğayı Hisset",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onProfileClick() }
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(end = 8.dp)) {
                    Text(
                        text = "XP ${progress.totalScore}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(2.dp, CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profil",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UrbanBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
            .height(72.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .blur(25.dp)
                .clip(RoundedCornerShape(32.dp)),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            tonalElevation = 8.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            )
        ) {}

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            NavItem(
                icon = Icons.Default.Map,
                label = "Harita",
                selected = currentRoute == Screen.Map.route,
                onClick = { onNavigate(Screen.Map.route) }
            )

            NavItem(
                icon = Icons.Default.CameraAlt,
                label = "Keşfet",
                selected = currentRoute == Screen.Camera.route,
                onClick = { onNavigate(Screen.Camera.route) }
            )

            NavItem(
                icon = Icons.Default.LibraryBooks,
                label = "Kitaplığım",
                selected = currentRoute == Screen.Library.route,
                onClick = { onNavigate(Screen.Library.route) }
            )
            
            NavItem(
                icon = Icons.Default.EmojiEvents,
                label = "Başarımlar",
                selected = currentRoute == Screen.Profile.route,
                onClick = { onNavigate(Screen.Profile.route) }
            )
        }
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(26.dp)
        )
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
