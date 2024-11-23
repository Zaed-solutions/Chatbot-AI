package com.zaed.chatbot.app.navigation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zaed.chatbot.ui.settings.SettingsScreen
import com.zaed.chatbot.ui.settings.font.FontScaleScreen

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    fontScale: Float,
    onFontScaleChanged: (Float) -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.SettingsRoute,
        enterTransition = {
            fadeIn(
                animationSpec = tween(
                    500, easing = LinearEasing
                )
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(
                    500, easing = LinearEasing
                )
            )
        }
    ) {
        composable<Route.SettingsRoute> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFontScale = { navController.navigate(Route.ChangeFontScaleRoute) }
            )
        }
        composable<Route.ChangeFontScaleRoute> {
            FontScaleScreen(
                fontScale = fontScale,
                onFontScaleChanged = onFontScaleChanged,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}