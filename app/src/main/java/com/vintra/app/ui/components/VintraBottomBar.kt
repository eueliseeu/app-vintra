package com.vintra.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onCreatePost: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = BottomTab.entries.indexOf(selectedTab)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val count = BottomTab.entries.size
            val horizontalPadding = 12.dp.toPx()
            val itemWidth = 52.dp.toPx()

            val gap =
                (size.width - horizontalPadding * 2 - itemWidth * count) /
                        (count + 1)

            val centerX =
                horizontalPadding +
                        gap +
                        itemWidth / 2 +
                        selectedIndex * (itemWidth + gap)

            //TODO: Lembrar de alterar o circulo black
            val radius = 38.dp.toPx()
            val depth = 47.dp.toPx()
            val corner = 18.dp.toPx()

            val path = Path().apply {
                moveTo(corner, 0f)

                lineTo(centerX - radius, 0f)

                cubicTo(
                    centerX - radius,
                    32.dp.toPx(),
                    centerX - 26.dp.toPx(),
                    depth,
                    centerX,
                    depth
                )

                cubicTo(
                    centerX + 26.dp.toPx(),
                    depth,
                    centerX + radius,
                    32.dp.toPx(),
                    centerX + radius,
                    0f
                )

                lineTo(size.width - corner, 0f)

                quadraticTo(
                    size.width,
                    0f,
                    size.width,
                    corner
                )

                lineTo(
                    size.width,
                    size.height - corner
                )

                quadraticTo(
                    size.width,
                    size.height,
                    size.width - corner,
                    size.height
                )

                lineTo(corner, size.height)

                quadraticTo(
                    0f,
                    size.height,
                    0f,
                    size.height - corner
                )

                lineTo(0f, corner)

                quadraticTo(
                    0f,
                    0f,
                    corner,
                    0f
                )

                close()
            }

            drawPath(
                path = path,
                color = FieldBackground
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab.entries.forEach { tab ->
                Box(
                    modifier = Modifier.size(52.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (tab != BottomTab.HOME) {
                        if (tab != selectedTab) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .clickable { onTabSelected(tab) },
                                contentAlignment = Alignment.Center
                            ) {
                                IconContent(
                                    tab = tab,
                                    color = Color(0xFF858585)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .offset(y = (10).dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    drawCircle(Color.White)
                                }

                                IconContent(
                                    tab = tab,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .size(52.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onCreatePost),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "New post",
                tint = Color.Black
            )
        }
    }
}

@Composable
private fun IconContent(
    tab: BottomTab,
    color: Color
) {
    when (val icon = tab.icon) {
        is TabIcon.Vector -> {
            Icon(
                imageVector = icon.imageVector,
                contentDescription = tab.contentDescription,
                tint = color,
                modifier = Modifier.size(21.dp)
            )
        }

        is TabIcon.Drawable -> {
            Icon(
                painter = painterResource(icon.resId),
                contentDescription = tab.contentDescription,
                tint = color,
                modifier = Modifier.size(21.dp)
            )
        }
    }
}
