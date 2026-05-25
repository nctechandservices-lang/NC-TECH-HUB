package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.Screen

@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()

    var active2FA by remember { mutableStateOf(currentUser?.isTwoFactorEnabled ?: false) }

    // Keep active 2FA sync with database modifications
    LaunchedEffect(currentUser) {
        currentUser?.let {
            active2FA = it.isTwoFactorEnabled
        }
    }

    var showUpgradeDialog by remember { mutableStateOf(false) }
    val userPlan = currentUser?.planName ?: "Basic"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("settings_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header is outside scrollable body
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (currentUser?.role == "Admin") {
                            viewModel.navigateTo(Screen.AdminPanel)
                        } else {
                            viewModel.navigateTo(Screen.MainDashboard)
                        }
                    },
                    modifier = Modifier.testTag("back_to_dashboard_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back logo"
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "System Preferences",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Scrollable preference items Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                // User Session Profile Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentUser?.role == "Admin") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                contentDescription = "User Head avatar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.username ?: "Client Session",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = currentUser?.email ?: "support@nctech.com",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Plan highlight
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF381E72), // bg-[#381E72]
                                border = BorderStroke(1.dp, Color(0xFF49454F))
                            ) {
                                Text(
                                    text = "SUBSCRIBER PLAN: ${currentUser?.planName?.uppercase() ?: "BASIC"}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEADDFF),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                // Theme Mode Custom selectors
                Text(
                    text = "Select Theme Mode",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple("System", Icons.Default.SettingsBrightness, "theme_system_btn"),
                        Triple("Light", Icons.Default.LightMode, "theme_light_btn"),
                        Triple("Dark", Icons.Default.DarkMode, "theme_dark_btn")
                    ).forEach { themeOpt ->
                        val isSelectedTheme = themeMode == themeOpt.first
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelectedTheme) 2.dp else 1.dp,
                                    color = if (isSelectedTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setTheme(themeOpt.first) }
                                .testTag(themeOpt.third),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelectedTheme) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = themeOpt.second,
                                    contentDescription = themeOpt.first,
                                    tint = if (isSelectedTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = themeOpt.first,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelectedTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Two Factor security setting
                Text(
                    text = "Security Options",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 12.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield Security Indicator",
                            tint = if (active2FA) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Two-Factor Verification",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Logins require random 6-digit OTP codes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Switch(
                            checked = active2FA,
                            onCheckedChange = {
                                active2FA = it
                                viewModel.toggle2FA(it)
                            },
                            modifier = Modifier.testTag("two_factor_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Navigation shortcuts
                if (currentUser?.role == "Admin") {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.AdminPanel) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .height(48.dp)
                            .testTag("enter_admin_deck_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = "Admin core logo")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Enter Admin Control suite", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Log out session
                OutlinedButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(bottom = 12.dp)
                        .testTag("logout_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = "Exit logo")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exit Current Session", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Sleek Custom Bottom Navigation
            SleekBottomNav(
                currentScreen = Screen.Settings,
                onNavigate = { viewModel.navigateTo(it) },
                onPlansClick = { showUpgradeDialog = true },
                isAdmin = currentUser?.role == "Admin"
            )
        }

        // UPGRADE PLANS DIALOG POPUP inside Settings Screen
        if (showUpgradeDialog) {
            AlertDialog(
                onDismissRequest = { showUpgradeDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "Crown Level",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "NC Tech Portal Upgrade",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Switch levels instantly to unlock restricted business modules on your device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Option 1: Basic
                        UpgradeChoiceCard(
                            title = "Basic Subscription (Free)",
                            desc = "Aadhaar Card, PAN, Voter Cards, passenger/commercial vehicle showroom catalog access.",
                            isSelected = userPlan == "Basic",
                            onClick = {
                                viewModel.purchasePlan("Basic")
                                showUpgradeDialog = false
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Option 2: Premium
                        UpgradeChoiceCard(
                            title = "Premium Plan (₹499/mo)",
                            desc = "Unlocks vehicle insurance quotes, Comprehensive Liability cover setup, third party claims desk.",
                            isSelected = userPlan == "Premium",
                            onClick = {
                                viewModel.purchasePlan("Premium")
                                showUpgradeDialog = false
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Option 3: Enterprise
                        UpgradeChoiceCard(
                            title = "Enterprise Plan (₹1499/mo)",
                            desc = "Full unrestricted coverage. Unlocks DevOps systems, cybersecurity checks, website designs, custom software coding audits.",
                            isSelected = userPlan == "Enterprise",
                            onClick = {
                                viewModel.purchasePlan("Enterprise")
                                showUpgradeDialog = false
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showUpgradeDialog = false }) {
                        Text("Dimiss Options", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("upgrade_plan_dialog")
            )
        }
    }
}
