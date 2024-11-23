package com.zaed.chatbot.ui.mainchat.components

import com.zaed.chatbot.R
private val makeMoneySuggestion = listOf(
    Suggestion(R.string.generate_fast_income, R.string.strategies, R.string.suggest_some_fast_income_strategies_for_me),
    Suggestion(R.string.create_a_business_plan, R.string.for_your_startup, R.string.i_need_some_help_creating_business_plan_for_my_startup),
    Suggestion(R.string.market_your_product, R.string.to_boost_sales, R.string.how_can_i_market_my_product_to_boost_sales),
    Suggestion(R.string.improve_seo, R.string.of_your_website, R.string.i_need_some_help_improving_the_seo_of_my_website),
    Suggestion(R.string.prepare_for_a_promotion, R.string.at_work, R.string.how_can_i_prepare_for_a_promotion_at_work),
    Suggestion(R.string.practice_for_interviews, R.string.for_success, R.string.what_is_a_good_way_to_practice_for_interviews),
    Suggestion(R.string.analyaze_competitors, R.string.for_insights, R.string.help_me_analyze_my_competitors)
)
private val haveFunSuggestions = listOf(
    Suggestion(R.string.share_trivia_facts, R.string.for_fun, R.string.share_trivia_facts_prompt),
    Suggestion(R.string.roleplay_a_character, R.string.for_creativity, R.string.roleplay_a_character_prompt),
    Suggestion(R.string.play_word_games, R.string.for_entertainment, R.string.play_word_games_prompt),
    Suggestion(R.string.tell_a_joke, R.string.for_fun, R.string.tell_a_joke_prompt),
    Suggestion(R.string.suggest_fun_activities, R.string.for_enjoyment, R.string.suggest_fun_activities_prompt),
)
enum class SuggestionCategory(val titleRes: Int, val suggestions: List<Suggestion>){
    MAKE_MONEY(
        R.string.make_money,
        makeMoneySuggestion
    ),
    HAVE_FUN(
        R.string.have_fun,
        haveFunSuggestions
    ),
    SOCIAL_MEDIA(
        R.string.social_media,
        emptyList()
    ),
    DO_HOMEWORK(
        R.string.do_homework,
        emptyList()
    ),
    LEARN_LANGUAGE(
        R.string.learn_a_language,
        emptyList()
    ),
    UTILITY(
        R.string.utility,
        emptyList()
    ),
    SEE_THE_FUTURE(
        R.string.see_the_future,
        emptyList()
    ),
    PHYSICAL_HEALTH(
        R.string.physical_health,
        emptyList()
    ),
    MENTAL_HEALTH(
        R.string.mental_health,
        emptyList()
    )
}

data class Suggestion(
    val titleRes: Int = 0,
    val subtitleRes: Int = 0,
    val promptRes: Int = 0,
)