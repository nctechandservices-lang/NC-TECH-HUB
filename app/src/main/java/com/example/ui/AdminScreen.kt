package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.Screen

@Composable
fun AdminScreen(viewModel: AppViewModel) {
    val allUsers by viewModel.allUsers.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()

    // Form states for Admin Settings
    var editContact by remember { mutableStateOf("") }
    var editAltContact by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editSupportEmail by remember { mutableStateOf("") }
    var editHeadline by remember { mutableStateOf("") }
    var editSubHeadline by remember { mutableStateOf("") }
    var editFooter by remember { mutableStateOf("") }
    var editAnnouncement by remember { mutableStateOf("") }
    var editAvatarType by remember { mutableStateOf("") }

    // Initialize fields once from DB Config
    LaunchedEffect(adminConfig) {
        editContact = adminConfig.contactNo
        editAltContact = adminConfig.altContactNo
        editEmail = adminConfig.email
        editSupportEmail = adminConfig.supportEmail
        editHeadline = adminConfig.appHeadline
        editSubHeadline = adminConfig.appSubHeadline
        editFooter = adminConfig.infoFooter
        editAnnouncement = adminConfig.customAnnouncement
        editAvatarType = adminConfig.adminAvatarType
    }

    var selectedTab by remember { mutableStateOf("METRICS") } // "METRICS", "PROFILE_DP", "PORTAL_CONFIG"
    val scrollState0 = rememberScrollState()

    val presetAvatars = listOf(
        Triple("avatar_boss", "Tech Boss", Icons.Default.Engineering),
        Triple("avatar_agent", "Cyber Agent", Icons.Default.SupportAgent),
        Triple("avatar_guru", "Dev Guru", Icons.Default.Terminal),
        Triple("avatar_shield", "Support Shield", Icons.Default.Security)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("admin_panel_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Admin Panel Titlebar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile DP
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val activeIcon = presetAvatars.find { it.first == adminConfig.adminAvatarType }?.third ?: Icons.Default.Person
                        Icon(
                            imageVector = activeIcon,
                            contentDescription = "Admin Avatar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin Control Suite",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Authority Mode Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    // Exit to settings / profile switch
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.Settings) },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "System Settings Options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Triple Tab Navigation Switches
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("METRICS", "User Databases"),
                    Pair("PROFILE_DP", "Manage Avatar (DP)"),
                    Pair("PORTAL_CONFIG", "Deploy Parameters")
                ).forEach { tab ->
                    val isSelected = selectedTab == tab.first
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .border(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedTab = tab.first }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.second,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Central Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState0)
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    "METRICS" -> {
                        // DB USERS METRIC LIST -- STRICTLY FOR ADMIN VIEWS
                        Text(
                            text = "SECURE SUBSCRIBERS DIRECTORY -- RESTRICTED",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 12.dp).testTag("all_users_count")
                        )

                        allUsers.forEach { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                                    .border(
                                        1.dp,
                                        if (user.role == "Admin") MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .testTag("admin_user_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (user.role == "Admin") MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.05f)
                                    else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (user.role == "Admin") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                        contentDescription = "User Icon",
                                        tint = if (user.role == "Admin") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = user.username,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = user.email,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                        if (user.mandatoryDetailsFilled) {
                                            Text(
                                                text = "Business: ${user.businessName} | Phone: ${user.contactNo} | GST: ${user.gstNo}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        } else {
                                            Text(
                                                text = "⚠️ Profile Details Pending",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(horizontalAlignment = Alignment.End) {
                                        // Level chip
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = when (user.planName) {
                                                "Enterprise" -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
                                                "Premium" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                                else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.12f)
                                            },
                                            border = BorderStroke(
                                                1.dp,
                                                when (user.planName) {
                                                    "Enterprise" -> MaterialTheme.colorScheme.tertiary
                                                    "Premium" -> MaterialTheme.colorScheme.primary
                                                    else -> MaterialTheme.colorScheme.outline
                                                }
                                            )
                                        ) {
                                            Text(
                                                text = user.planName,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (user.planName) {
                                                    "Enterprise" -> MaterialTheme.colorScheme.tertiary
                                                    "Premium" -> MaterialTheme.colorScheme.primary
                                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                },
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (user.isTwoFactorEnabled) Icons.Default.Lock else Icons.Default.EnhancedEncryption,
                                                contentDescription = "2FA status icon",
                                                tint = if (user.isTwoFactorEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (user.isTwoFactorEnabled) "2FA" else "No 2FA",
                                                fontSize = 9.sp,
                                                color = if (user.isTwoFactorEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "PROFILE_DP" -> {
                        // REGISTERED ADMIN AVATAR DP CHANGER
                        // fulfills "admin apne hisab se DP change kar sake"
                        Text(
                            text = "ADMIN PORTION CONFIG: ADJUST DP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Pick active preset cyber tags to redefine your DP across security dashboards.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        presetAvatars.forEach { preset ->
                            val isSelectedPreset = editAvatarType == preset.first
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        width = if (isSelectedPreset) 2.dp else 1.dp,
                                        color = if (isSelectedPreset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { editAvatarType = preset.first }
                                    .testTag("avatar_selector_preset"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelectedPreset) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = preset.third,
                                        contentDescription = preset.second,
                                        tint = if (isSelectedPreset) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = preset.second,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (isSelectedPreset) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Active Preset",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.updateAdminConfig(
                                    contact = editContact,
                                    altContact = editAltContact,
                                    email = editEmail,
                                    supportEmail = editSupportEmail,
                                    headline = editHeadline,
                                    subHeadline = editSubHeadline,
                                    footer = editFooter,
                                    avatarType = editAvatarType,
                                    customAvatarUri = "",
                                    announcement = editAnnouncement
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Selected DP", fontWeight = FontWeight.Bold)
                        }
                    }

                    "PORTAL_CONFIG" -> {
                        // PORTAL CONFIG METADATA WRITER
                        // Fulfills "and mandatory details fill kare ... data jo bhi ho sirf admin ko dikhna chahiye"
                        Text(
                            text = "DEPLOY MANDATORY CLIENT-facing DETAILS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Text(
                            text = "Update the brand text parameters displayed directly to clients on the portal.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = editHeadline,
                            onValueChange = { editHeadline = it },
                            label = { Text("Primary Brand Title") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("admin_headline_field"),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = editSubHeadline,
                            onValueChange = { editSubHeadline = it },
                            label = { Text("Secondary Description Subtitle") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = editContact,
                            onValueChange = { editContact = it },
                            label = { Text("Primary Phone line (+91 6353161655)") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).testTag("admin_contact_field"),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = editAltContact,
                            onValueChange = { editAltContact = it },
                            label = { Text("Secondary Backup Phone (+91 8200781240)") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Executive Email Details") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = editSupportEmail,
                            onValueChange = { editSupportEmail = it },
                            label = { Text("Technical Desk Support Email") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = editAnnouncement,
                            onValueChange = { editAnnouncement = it },
                            label = { Text("System Announcement Board Banner") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = editFooter,
                            onValueChange = { editFooter = it },
                            label = { Text("System branding footer line") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = {
                                viewModel.updateAdminConfig(
                                    contact = editContact,
                                    altContact = editAltContact,
                                    email = editEmail,
                                    supportEmail = editSupportEmail,
                                    headline = editHeadline,
                                    subHeadline = editSubHeadline,
                                    footer = editFooter,
                                    avatarType = editAvatarType,
                                    customAvatarUri = "",
                                    announcement = editAnnouncement
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("admin_save_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudUpload, contentDescription = "Upload Logo")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Deploy Portal Updates", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
