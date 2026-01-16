package com.yangwan.htimer.ui.timer.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun TimerDrawerSheet(
    onNavigate: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val drawerWidth = (screenWidth * 0.75f).coerceIn(260.dp, 310.dp)

    ModalDrawerSheet(
        modifier = Modifier.requiredWidth(drawerWidth),
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
        windowInsets = WindowInsets.statusBars
    ) {
        Spacer(Modifier.height(12.dp))

        Text(
            text = "HTimer",
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        DrawerItem("计时设置", Icons.Rounded.Settings) { onNavigate("settings") }
        DrawerItem("历史数据", Icons.Rounded.Refresh) { onNavigate("history") }
        DrawerItem("统计图表", Icons.AutoMirrored.Rounded.List) { onNavigate("stats") }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 28.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        DrawerItem("关于软件", Icons.Rounded.Info) { onNavigate("about") }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = FontWeight.Medium) },
        selected = false,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp)) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = androidx.compose.ui.graphics.Color.Transparent
        )
    )
}