package com.vintra.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vintra.app.ui.navigation.BottomTab
import com.vintra.app.ui.navigation.TabIcon
import com.vintra.app.ui.theme.FieldBackground

@Composable
fun VintraBottomBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val barHeight = 70.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(barHeight),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val corner = 18.dp.toPx()
                    val barPath = Path().apply {
                        moveTo(corner, 0f)
                        lineTo(size.width - corner, 0f)
                        quadraticTo(size.width, 0f, size.width, corner)
                        lineTo(size.width, size.height - corner)
                        quadraticTo(size.width, size.height, size.width - corner, size.height)
                        lineTo(corner, size.height)
                        quadraticTo(0f, size.height, 0f, size.height - corner)
                        lineTo(0f, corner)
                        quadraticTo(0f, 0f, corner, 0f)
                        close()
                    }
                    drawPath(path = barPath, color = FieldBackground)
                }
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab.entries.forEach { tab ->
                BottomBarItem(
                    tab = tab,
                    isSelected = tab == selectedTab,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    tab: BottomTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color.Black else Color(0xFF858585),
        label = "color"
    )

    Box(
        modifier = Modifier
            .size(52.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF121212))
                    .border(width = 3.dp, color = Color.Black, shape = CircleShape)
            )

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                IconContent(tab = tab, color = iconColor)
            }
        } else {
            IconContent(tab = tab, color = iconColor)
        }
    }
}

@Composable
private fun IconContent(tab: BottomTab, color: Color) {
    when (val icon = tab.icon) {
        is TabIcon.Vector -> Icon(
            imageVector = icon.imageVector,
            contentDescription = tab.contentDescription,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        is TabIcon.Drawable -> Icon(
            painter = painterResource(icon.resId),
            contentDescription = tab.contentDescription,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}