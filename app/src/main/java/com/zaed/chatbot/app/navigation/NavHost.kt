package com.zaed.chatbot.app.navigation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.compiler.plugins.kotlin.EmptyFunctionMetrics.composable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.zaed.chatbot.ui.settings.SettingsScreen
import com.zaed.chatbot.ui.settings.faq.FaqSupportScreen
import com.zaed.chatbot.ui.settings.font.FontScaleScreen
import com.zaed.chatbot.ui.settings.guidelines.CommunityGuidelinesScreen
import com.zaed.chatbot.ui.settings.language.ChangeLanguageScreen
import com.zaed.chatbot.ui.settings.mode.ChatModeScreen
import com.zaed.chatbot.ui.settings.privacy.PrivacyPolicyScreen
import com.zaed.chatbot.ui.settings.promocode.PromoCodeScreen
import com.zaed.chatbot.ui.settings.rate.RateUsScreen
import com.zaed.chatbot.ui.settings.restore.RestorePurchaseScreen
import com.zaed.chatbot.ui.settings.terms.TermsOfUseScreen
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.zaed.chatbot.ui.mainchat.MainChatScreen

@Composable
fun NavigationHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    fontScale: Float,
    onFontScaleChanged: (Float) -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    NavHost(
        modifier = modifier,
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
        }
    ) {
        composable<Route.MainChatRoute> {
            MainChatScreen(
                onNavigateToPersonalizationScreen = {/*TODO*/},
                onNavigateToHistoryScreen = {/*TODO*/},
                onNavigateToSettingsScreen = {/*TODO*/},
                onNavigateToPrivacyAndTerms = {/*TODO*/}
            )
        }
    }
}
        composable<Route.SettingsRoute> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFontScale = { navController.navigate(Route.ChangeFontScaleRoute) },
                onNavigateToChatMode = { navController.navigate(Route.ChangeChatModeRoute) },
                onNavigateToLanguage = { navController.navigate(Route.ChangeLanguageRoute) },
                onNavigateToPromoCode = { navController.navigate(Route.PromoCodeRoute) },
                onNavigateToRateUs = { navController.navigate(Route.RateUsRoute) },
                onNavigateToRestorePurchase = { navController.navigate(Route.RestorePurchaseRoute) },
                onNavigateToFaqSupport = { navController.navigate(Route.FaqSupportRoute) },
                onNavigateToTermsOfUse = { navController.navigate(Route.TermsOfUseRoute) },
                onNavigateToPrivacyPolicy = { navController.navigate(Route.PrivacyPolicyRoute) },
                onNavigateToCommunityGuidelines = { navController.navigate(Route.CommunityGuidelinesRoute) }
            )
        }
        composable<Route.ChangeFontScaleRoute> {
            FontScaleScreen(
                fontScale = fontScale,
                onFontScaleChanged = onFontScaleChanged,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<Route.ChangeChatModeRoute> {
            ChatModeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
            // Chat Mode Screen
        }
        composable<Route.ChangeLanguageRoute> {
            ChangeLanguageScreen(
                onNavigateBack = { navController.popBackStack() },
                onLanguageSelected = onLanguageSelected
            )
            // Language Screen
        }
        composable<Route.PromoCodeRoute> {
            PromoCodeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
            // Promo Code Screen
        }
        composable<Route.RateUsRoute> {
            RateUsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
            // Rate Us Screen
        }
        composable<Route.RestorePurchaseRoute> {
            RestorePurchaseScreen(
                onNavigateBack = { navController.popBackStack() }
            )
            // Restore Purchase Screen
        }
        composable<Route.FaqSupportRoute> {
            FaqSupportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
            // FAQ & Support Screen
        }
        composable<Route.TermsOfUseRoute> {
            TermsOfUseScreen(
                onNavigateBack = { navController.popBackStack() }
            )
            // Terms of Use Screen
        }
        composable<Route.PrivacyPolicyRoute> {
            PrivacyPolicyScreen(
                onNavigateBack = { navController.popBackStack() }
            )
            // Privacy Policy Screen
        }
        composable<Route.CommunityGuidelinesRoute> {
            CommunityGuidelinesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}

