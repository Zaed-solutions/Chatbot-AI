package com.zaed.chatbot.ui.mainchat.components

import com.aallam.openai.api.model.ModelId
import com.zaed.chatbot.R
import com.zaed.chatbot.ui.mainchat.MainChatUiAction

enum class ChatModel(val modelId:ModelId,val nameRes: Int, val descriptionRes: Int, val iconRes: Int, val isPaid: Boolean) {
    GPT_4O_MINI(ModelId("gpt-4o-mini"),R.string.gpt_4o_mini, R.string.smart_and_fast, R.drawable.ic_star, false),
    GPT_4O(ModelId("gpt-4o"),R.string.gpt_4o, R.string.newest_and_fastest, R.drawable.ic_double_star, true),
    AI_ART_GENERATOR(ModelId("dall-e-2"),R.string.ai_art_generator, R.string.create_images, R.drawable.ic_image_1, true),
}