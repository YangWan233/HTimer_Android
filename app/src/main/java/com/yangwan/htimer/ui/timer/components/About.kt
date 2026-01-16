@file:Suppress("DEPRECATION")

package com.yangwan.htimer.ui.timer.components

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yangwan.htimer.R

private fun getAppVersionName(context: Context): String {
    return try {
        val packageManager = context.packageManager
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(context.packageName, 0)
        }
        packageInfo.versionName ?: "Unknown"
    } catch (_: Exception) {
        "N/A"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text("关于应用", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        val contentModifier = Modifier.padding(padding).fillMaxSize()

        if (isLandscape) {
            Row(
                modifier = contentModifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    AppInfoHero()
                }
                Box(modifier = Modifier.weight(1.2f)) {
                    AboutLinksList(onLinkClick = { url -> uriHandler.openUri(url) })
                }
            }
        } else {
            LazyColumn(
                modifier = contentModifier,
                contentPadding = PaddingValues(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { AppInfoHero() }
                item { Spacer(modifier = Modifier.height(24.dp)) }
                item { AboutLinksList(onLinkClick = { url -> uriHandler.openUri(url) }) }
            }
        }
    }
}

@Composable
fun AppInfoHero() {
    val context = LocalContext.current
    val versionName = remember { getAppVersionName(context) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Unspecified
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "H-Timer",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
        )
        Text(
            text = "Version $versionName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AboutLinksList(onLinkClick: (String) -> Unit) {
    val links = listOf(
        AboutItemData("源代码", "GitHub 开源仓库", Icons.Default.Info, "https://github.com/YangWan233/HTimer_Android"),
        AboutItemData("用户反馈", "提交 Bug 或建议", Icons.Default.Warning, "https://github.com/YangWan233/HTimer_Android/issues"),
        AboutItemData("特别鸣谢", "贡献者与开源库", Icons.Default.Favorite, ""),
        AboutItemData("隐私政策", "了解数据处理方式", Icons.Default.Lock, "")
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        links.forEach { item ->
            AboutClickableCard(item) {
                if (item.url.isNotEmpty()) {
                    onLinkClick(item.url)
                }
            }
        }
    }
}

data class AboutItemData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val url: String
)

@Composable
fun AboutClickableCard(data: AboutItemData, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(data.icon, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    data.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    data.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}