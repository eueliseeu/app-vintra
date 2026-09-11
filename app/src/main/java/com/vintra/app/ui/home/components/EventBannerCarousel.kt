package com.vintra.app.ui.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vintra.app.R
import kotlinx.coroutines.delay

private val EVENT_BANNERS = listOf(
    R.drawable.evento,
    R.drawable.evento1
)

private const val BANNER_ROTATE_INTERVAL_MS = 4000L
private val BANNER_WIDTH = 330.dp
private val BANNER_HEIGHT = 150.dp
private val FADE_HEIGHT = 30.dp

@Composable
fun EventBannerCarousel(modifier: Modifier = Modifier) {
    if (EVENT_BANNERS.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(BANNER_ROTATE_INTERVAL_MS)
            currentIndex = (currentIndex + 1) % EVENT_BANNERS.size
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.background

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .width(BANNER_WIDTH)
                .height(BANNER_HEIGHT)
                .clip(RoundedCornerShape(14.dp))
        ) {
            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = {
                    (
                            slideInVertically(animationSpec = tween(600)) { fullHeight -> fullHeight } +
                                    fadeIn(animationSpec = tween(600))
                            ).togetherWith(
                            slideOutVertically(animationSpec = tween(600)) { fullHeight -> -fullHeight } +
                                    fadeOut(animationSpec = tween(600))
                        )
                },
                label = "event_banner_carousel"
            ) { index ->
                Image(
                    painter = painterResource(id = EVENT_BANNERS[index]),
                    contentDescription = "Event banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(BANNER_WIDTH)
                        .height(BANNER_HEIGHT)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(BANNER_WIDTH)
                .height(FADE_HEIGHT)
                .offset(y = (FADE_HEIGHT / 2))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(backgroundColor.copy(alpha = 0f), backgroundColor)
                    )
                )
        )
    }
}