package com.zaed.chatbot.app.navigation

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.android.billingclient.api.ProductDetails
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
    isPro: Boolean = false,
    products: List<ProductDetails> = emptyList(),
    fontScale: Float,
    onSubscriptionAction: (SubscriptionAction) -> Unit,
    onFontScaleChanged: (Float) -> Unit,
    onDefaultChatModeChanged: (ChatModel) -> Unit,
    onDecrementFreeTrialCount: () -> Unit,
    freeTrialCount: Int,
) {
    Log.d("tenoo", "naviHos: ${isPro}")
    NavHost(modifier = modifier,
        navController = navController,
        startDestination = MainChatRoute(),
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
        composable<MainChatRoute> { backStackEntry ->
            val args: MainChatRoute = backStackEntry.toRoute()
            MainChatScreen(
                onDecrementFreeTrialCount = onDecrementFreeTrialCount,
                freeTrialCount = freeTrialCount,
                chatId = args.chatId,
                onNavigateToPersonalizationScreen = {/*TODO*/ },
                onNavigateToHistoryScreen = {
                    navController.navigate(HistoryRoute)
                },
                isPro = isPro,
                products = products,
                onSubscriptionAction = onSubscriptionAction,
                onNavigateToSettingsScreen = { navController.navigate(SettingsRoute) },
                onNavigateToPrivacyAndTerms = { navController.navigate(PrivacyPolicyRoute) },
            )
        }
        composable<SettingsRoute> {
            SettingsScreen(
                isPro = isPro,
                products = products,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFontScale = { navController.navigate(ChangeFontScaleRoute) },
                onNavigateToChatMode = { navController.navigate(ChangeChatModeRoute) },
                onNavigateToPromoCode = { navController.navigate(PromoCodeRoute) },
                onNavigateToFaqSupport = { navController.navigate(FaqSupportRoute) },
                onNavigateToPrivacyPolicy = { navController.navigate(PrivacyPolicyRoute) },
                onNavigateToCommunityGuidelines = {
                    navController.navigate(
                        CommunityGuidelinesRoute
                    )
                }, onSubscriptionAction = onSubscriptionAction
            )
        }
        composable<ChangeFontScaleRoute> {
            FontScaleScreen(fontScale = fontScale,
                onFontScaleChanged = onFontScaleChanged,
                onNavigateBack = { navController.popBackStack() })
        }
        composable<ChangeChatModeRoute> {
            ChatModeScreen(
                isPro = isPro,
                products = products,
                defaultChatMode = defaultChatMode,
                onSetDefaultChatMode = {
                    onDefaultChatModeChanged(it)
                },
                onNavigateBack = { navController.popBackStack() },
                onSubscriptionAction = onSubscriptionAction
            )
        }
        composable<HistoryRoute> {
            HistoryScreen(
                onBackPressed = { navController.popBackStack() },
                onNavigateToChat = { chatId ->
                    //todo: use chat id to load chat in main chat
                    navController.navigate(MainChatRoute(chatId))
                }
            )
        }
        composable<PromoCodeRoute> {
            PromoCodeScreen(
                onNavigateBack = { navController.popBackStack() },
                onSubscriptionAction = onSubscriptionAction
            )
        }

        composable<FaqSupportRoute> {
            FaqSupportScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<PrivacyPolicyRoute> {
            PrivacyPolicyScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable<CommunityGuidelinesRoute> {
            CommunityGuidelinesScreen(onNavigateBack = { navController.popBackStack() })
        }
    }

}

