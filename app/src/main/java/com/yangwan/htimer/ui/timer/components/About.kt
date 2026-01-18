@file:Suppress("DEPRECATION")

package com.yangwan.htimer.ui.timer.components

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import com.yangwan.htimer.R

data class LicenseInfo(
    val name: String,
    val licenseName: String,
    val licenseText: String
)

private fun readAssetFile(context: Context, fileName: String): String {
    return try {
        context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        "无法加载协议内容，请检查 assets/$fileName 是否存在。"
    }
}

private fun getAppVersionName(context: Context): String {
    return try {
        val packageManager = context.packageManager
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
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
        val contentModifier = Modifier
            .padding(padding)
            .fillMaxSize()
        if (isLandscape) {
            Row(modifier = contentModifier.padding(horizontal = 24.dp)) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) { AppInfoHero() }
                Box(modifier = Modifier.weight(1.2f)) { AboutLinksList { uriHandler.openUri(it) } }
            }
        } else {
            LazyColumn(
                modifier = contentModifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { AppInfoHero() }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { AboutLinksList { uriHandler.openUri(it) } }
            }
        }
    }
}

@Composable
fun AppInfoHero() {
    val context = LocalContext.current
    val versionName = remember { getAppVersionName(context) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
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
    val context = LocalContext.current
    var showThanksDialog by remember { mutableStateOf(false) }
    var showLicenseListDialog by remember { mutableStateOf(false) }
    var selectedLicense by remember { mutableStateOf<LicenseInfo?>(null) }

    val twistyLicenseText = remember { readAssetFile(context, "LICENSE.txt") }

    val licenseList = listOf(
        LicenseInfo(
            "TwistyTimer",
            "GPL-3.0 License",
            twistyLicenseText
        ),
        LicenseInfo(
            "min2phase (GPLv3)",
            "GPL-3.0 License",
            """
                License GPLv3
                
                Copyright (C) 2023  Shuang Chen
                
                This program is free software: you can redistribute it and/or modify
                it under the terms of the GNU General Public License as published by
                the Free Software Foundation, either version 3 of the License, or
                (at your option) any later version.
                
                This program is distributed in the hope that it will be useful,
                but WITHOUT ANY WARRANTY; without even the implied warranty of
                MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
                GNU General Public License for more details.
                
                You should have received a copy of the GNU General Public License
                along with this program.  If not, see <http://www.gnu.org/licenses/>.
            """.trimIndent()
        ),
        LicenseInfo(
            "min2phase (MIT)",
            "MIT License",
            """
                License MIT
                
                Copyright (c) 2023 Chen Shuang
                
                Permission is hereby granted, free of charge, to any person obtaining a copy
                of this software and associated documentation files (the "Software"), to deal
                in the Software without restriction, including without limitation the rights
                to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                copies of the Software, and to permit persons to whom the Software is
                furnished to do so, subject to the following conditions:
                
                The above copyright notice and this permission notice shall be included in all
                copies or substantial portions of the Software.
                
                THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                SOFTWARE.
            """.trimIndent()
        )
    )

    val mainLinks = listOf(
        AboutItemData(
            "源代码",
            "GitHub 开源仓库",
            Icons.Default.Code,
            "https://github.com/YangWan233/HTimer_Android"
        ),
        AboutItemData(
            "用户反馈",
            "提交 Bug 或建议",
            Icons.Default.Warning,
            "https://github.com/YangWan233/HTimer_Android/issues"
        ),
        AboutItemData("特别鸣谢", "贡献者与参考项目", Icons.Default.Favorite, "action_thanks"),
        AboutItemData(
            "开源许可",
            "查看第三方库许可协议",
            Icons.Default.Description,
            "action_license"
        ),
        AboutItemData("隐私政策", "了解数据处理方式", Icons.Default.Lock, "")
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        mainLinks.forEach { item ->
            AboutClickableCard(item) {
                when (item.url) {
                    "action_thanks" -> showThanksDialog = true
                    "action_license" -> showLicenseListDialog = true
                    "" -> {}
                    else -> onLinkClick(item.url)
                }
            }
        }
    }

    if (showThanksDialog) {
        AlertDialog(
            onDismissRequest = { showThanksDialog = false },
            title = { Text("特别鸣谢") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "感谢以下开源项目提供的技术支持：",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val thanksProjects = listOf(
                        "aricneto/TwistyTimer" to "https://github.com/aricneto/TwistyTimer",
                        "cs0x7f/min2phase" to "https://github.com/cs0x7f/min2phase"
                    )

                    thanksProjects.forEach { (name, url) ->
                        Surface(
                            onClick = { onLinkClick(url); showThanksDialog = false },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Icon(
                                    Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showThanksDialog = false }) { Text("关闭") } }
        )
    }

    if (showLicenseListDialog) {
        AlertDialog(
            onDismissRequest = { showLicenseListDialog = false },
            title = { Text("开源许可") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    licenseList.forEach { info ->
                        Surface(
                            onClick = { selectedLicense = info; showLicenseListDialog = false },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(info.name, fontWeight = FontWeight.Bold)
                                    Text(
                                        info.licenseName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showLicenseListDialog = false
                }) { Text("取消") }
            }
        )
    }

    selectedLicense?.let { info ->
        AlertDialog(
            onDismissRequest = { selectedLicense = null },
            title = { Text(info.name) },
            text = {
                Box(
                    modifier = Modifier
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(info.licenseText, fontSize = 12.sp, lineHeight = 16.sp)
                }
            },
            confirmButton = { TextButton(onClick = { selectedLicense = null }) { Text("确定") } }
        )
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
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        data.icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
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