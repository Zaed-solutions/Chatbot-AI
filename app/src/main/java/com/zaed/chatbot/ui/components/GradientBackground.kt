package com.zaed.chatbot.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


fun Modifier.gradientBackground(
    colors: List<Color>,
    angle: Float
) = this.then(
    Modifier
        .drawBehind {
        val angleRad = angle / 180f * PI
        val x = cos(angleRad).toFloat() //Fractional x
        val y = sin(angleRad).toFloat() //Fractional y

        val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
        val offset = center + Offset(x * radius, y * radius)

        val exactOffset = Offset(
            x = minOf(offset.x.coerceAtLeast(0f), size.width),
            y = size.height - minOf(offset.y.coerceAtLeast(0f), size.height)
        )
        val gradientBrush = Brush.linearGradient(
            colors = colors,
            start = Offset(size.width, size.height) - exactOffset,
            end = exactOffset
        )

        // Clip the content to the specified shape and draw the gradient
            drawRect(
                brush = gradientBrush,
                size = size
            )
    }
)