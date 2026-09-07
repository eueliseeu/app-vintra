package com.vintra.app.ui.navigation


import androidx.compose.ui.graphics.vector.ImageVector
import com.vintra.app.R

sealed interface TabIcon {
    data class Vector(val imageVector: ImageVector) : TabIcon
    data class Drawable(val resId: Int) : TabIcon
}

enum class BottomTab(val icon: TabIcon, val contentDescription: String) {
    MONTHLY_SUMMARY(TabIcon.Drawable(R.drawable.weight), "Monthly balance"),
    STATEMENT(TabIcon.Drawable(R.drawable.file_text), "Statement"),
    HOME(TabIcon.Drawable(R.drawable.house), "Home"),
    RANKING(TabIcon.Drawable(R.drawable.trending_up), "Ranking"),
    PROFILE(TabIcon.Drawable(R.drawable.settings), "Settings"),

}