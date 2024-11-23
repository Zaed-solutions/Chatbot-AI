package com.zaed.chatbot.ui.mainchat.components

import com.zaed.chatbot.R
import com.zaed.chatbot.ui.mainchat.MainChatUiAction

enum class ChatModel(val nameRes: Int, val descriptionRes: Int, val iconRes: Int) {
    GPT_4O_MINI(R.string.gpt_4o_mini, R.string.smart_and_fast, R.drawable.ic_star),
    GPT_4O(R.string.gpt_4o, R.string.newest_and_fastest, R.drawable.ic_double_star),
    AI_ART_GENERATOR(R.string.ai_art_generator, R.string.create_images, R.drawable.ic_image_1),
}