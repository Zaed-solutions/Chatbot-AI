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
import com.zaed.chatbot.ui.activity.SubscriptionAction
import com.zaed.chatbot.ui.history.HistoryScreen
import com.zaed.chatbot.ui.mainchat.MainChatScreen
import com.zaed.chatbot.ui.mainchat.components.ChatModel
import com.zaed.chatbot.ui.settings.SettingsScreen
import com.zaed.chatbot.ui.settings.faq.FaqSupportScreen
import com.zaed.chatbot.ui.settings.font.FontScaleScreen
import com.zaed.chatbot.ui.settings.guidelines.CommunityGuidelinesScreen
import com.zaed.chatbot.ui.settings.mode.ChatModeScreen
import com.zaed.chatbot.ui.settings.privacy.PrivacyPolicyScreen
import com.zaed.chatbot.ui.settings.promocode.PromoCodeScreen

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    defaultChatMode: ChatModel,
    fontScale: Float,
    onSubscriptionAction: (SubscriptionAction) -> Unit,
    onFontScaleChanged: (Float) -> Unit,
    onDefaultChatModeChanged: (ChatModel) -> Unit,
) {
    NavHost(modifier = modifier,
        navController = navController,
        startDestination = Route.MainChatRoute,
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
        }) {
        composable<Route.MainChatRoute> {
            MainChatScreen(
                onNavigateToPersonalizationScreen = {/*TODO*/ },
                onNavigateToHistoryScreen = {
                    navController.navigate(Route.HistoryRoute)
                },
                onSubscriptionAction = onSubscriptionAction,
                onNavigateToSettingsScreen = { navController.navigate(Route.SettingsRoute) },
                onNavigateToPrivacyAndTerms = { navController.navigate(Route.PrivacyPolicyRoute) })
        }
        composable<Route.SettingsRoute> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFontScale = { navController.navigate(Route.ChangeFontScaleRoute) },
                onNavigateToChatMode = { navController.navigate(Route.ChangeChatModeRoute) },
                onNavigateToPromoCode = { navController.navigate(Route.PromoCodeRoute) },
                onNavigateToFaqSupport = { navController.navigate(Route.FaqSupportRoute) },
                onNavigateToPrivacyPolicy = { navController.navigate(Route.PrivacyPolicyRoute) },
                onNavigateToCommunityGuidelines = { navController.navigate(Route.CommunityGuidelinesRoute) },
                onSubscriptionAction = onSubscriptionAction
            )
        }
        composable<Route.ChangeFontScaleRoute> {
            FontScaleScreen(fontScale = fontScale,
                onFontScaleChanged = onFontScaleChanged,
                onNavigateBack = { navController.popBackStack() })
        }
        composable<Route.ChangeChatModeRoute> {
            ChatModeScreen(
                defaultChatMode = defaultChatMode,
                onSetDefaultChatMode = {
                    onDefaultChatModeChanged(it)
                },
                onNavigateBack = { navController.popBackStack() },
                onSubscriptionAction = onSubscriptionAction
            )
        }
        composable<Route.HistoryRoute> {
            HistoryScreen(
                onBackPressed = { navController.popBackStack() },
                onNavigateToChat = { chatId ->
                    //todo: use chat id to load chat in main chat
                    navController.navigate(Route.MainChatRoute)
                }
            )
        }
        composable<Route.PromoCodeRoute> {
            PromoCodeScreen(
                onNavigateBack = { navController.popBackStack() },
                onSubscriptionAction = onSubscriptionAction
            )
        }

        composable<Route.FaqSupportRoute> {
            FaqSupportScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<Route.PrivacyPolicyRoute> {
            PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<Route.CommunityGuidelinesRoute> {
            CommunityGuidelinesScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}

