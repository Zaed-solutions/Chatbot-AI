package com.zaed.chatbot.ui.mainchat.components

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun LatexView(
    modifier: Modifier = Modifier,
    latex: String,
    isBlock: Boolean
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isBlock) 80.dp else 35.dp),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()

                // Set layout parameters for inline/block
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                // Disable scrolling
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false

                // Set transparent background
                setBackgroundColor(0)
            }
        },
        update = { webView ->
            val escapedLatex = latex.replace("\\", "\\\\")
            val html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css">
                    <script src="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js"></script>
                    <style>
                        body {
                            margin: 0;
                            padding: 0;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            background-color: transparent;
                            min-height: ${if (isBlock) "80px" else "35px"};
                            overflow: hidden;
                        }
                        #formula-container {
                            display: ${if (isBlock) "block" else "inline"};
                            width: ${if (isBlock) "100%" else "auto"};
                            text-align: center;
                        }
                        .katex {
                            font-size: ${if (isBlock) "1.21em" else "1.1em"};
                        }
                    </style>
                </head>
                <body>
                    <div id="formula-container">
                        <span id="formula"></span>
                    </div>
                    <script>
                        katex.render(`$escapedLatex`, document.getElementById('formula'), {
                            displayMode: ${isBlock},
                            throwOnError: false,
                            strict: false
                        });
                    </script>
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }
    )
}
