package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.Screen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DashboardScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()

    var showUpgradeDialog by remember { mutableStateOf(false) }
    var selectedServiceCategory by remember { mutableStateOf("ALL") } // "ALL", "VEHICLES", "SEVA", "INSURANCE", "IT"
    var showInquiryDialog by remember { mutableStateOf<String?>(null) }
    var inquiryName by remember { mutableStateOf("") }
    var inquirySpec by remember { mutableStateOf("") }
    var inquiryMessage by remember { mutableStateOf("") }

    val userPlan = currentUser?.planName ?: "Basic"

    // Help coordinate which levels see which categories
    val canAccessInsurance = userPlan == "Premium" || userPlan == "Enterprise"
    val canAccessIT = userPlan == "Enterprise"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("user_dashboard_container")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // 1. Sleek Profile Header Row (Translating the HTML header perfectly)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Image placeholder with absolute overlay edit button
                    Box(
                        modifier = Modifier.size(56.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            color = Color(0xFFD0BCFF), // bg-[#D0BCFF]
                            border = BorderStroke(2.dp, Color(0xFF49454F)) // border-[#49454F]
                        ) {
                            // First character of name or custom placeholder
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (currentUser?.username ?: "Client").take(1).uppercase(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF381E72) // text-[#381E72]
                                )
                            }
                        }
                        
                        // Edit / camera badge button
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(Color(0xFFD0BCFF), CircleShape)
                                .align(Alignment.BottomEnd)
                                .clickable {
                                    viewModel.triggerNotification("Admin Profile", "Ready to customize theme nodes.")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile Button",
                                tint = Color(0xFF381E72),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = currentUser?.username ?: "Aryan Sharma",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE6E1E5)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = Color(0xFF4F378B),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.padding(vertical = 1.dp)
                            ) {
                                Text(
                                    text = currentUser?.role?.uppercase() ?: "ADMIN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEADDFF),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "System Mode",
                                fontSize = 12.sp,
                                color = Color(0xFF938F99)
                            )
                        }
                    }
                }

                // Notification bell icon button
                IconButton(
                    onClick = {
                        viewModel.triggerNotification("System Toast", "Security systems operational. All nodes secured.")
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2B2930), CircleShape)
                        .testTag("notification_toggle_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFFD0BCFF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Announcement sub-strip
            Surface(
                color = Color(0xFF2B2930),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF49454F).copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Announcement Banner",
                        tint = Color(0xFFD0BCFF),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = adminConfig.customAnnouncement,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFE6E1E5).copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category Filter Switches
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Pair("ALL", "All Suites"),
                    Pair("VEHICLES", "Vehicles"),
                    Pair("SEVA", "Seva Kendra"),
                    Pair("INSURANCE", "Insurance"),
                    Pair("IT", "IT Portal")
                ).forEach { filter ->
                    val isSelected = selectedServiceCategory == filter.first
                    Text(
                        text = filter.second,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedServiceCategory = filter.first }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }

            // Scrollable Grid of Services
            val scrollStateGrid = rememberScrollState()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollStateGrid)
                    .padding(16.dp)
            ) {
                // EXCLUSIVE SLEEK THEME CARDS - RENDER ONCE AT TOP OF FEED (ALL SCREEN FILTER)
                if (selectedServiceCategory == "ALL") {
                    
                    // A. Current Plan Card bg-[#2B2930] rounded-3xl border-[#49454F]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .border(1.dp, Color(0xFF49454F), RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2930))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "CURRENT PLAN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF938F99),
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when (userPlan) {
                                            "Enterprise" -> "Enterprise Pro"
                                            "Premium" -> "Premium Corporate"
                                            else -> "Basic Tier"
                                        },
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD0BCFF)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF381E72), RoundedCornerShape(12.dp))
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "Subscription Level Icon",
                                        tint = Color(0xFFD0BCFF),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(Color(0xFF1C1B1F), RoundedCornerShape(16.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Feature Support Check",
                                        tint = Color(0xFFBDBDBD),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Unlimited 2FA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(Color(0xFF1C1B1F), RoundedCornerShape(16.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Feature Support Check",
                                        tint = Color(0xFFBDBDBD),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when (userPlan) {
                                            "Enterprise" -> "Admin Coding"
                                            "Premium" -> "Fleet Protection"
                                            else -> "Digital Seva"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    // B. Vivid Security Status Card bg-[#EADDFF] text-[#21005D] rounded-3xl
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEADDFF)),
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Security Shield Status",
                                        tint = Color(0xFF21005D)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Security Status",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF21005D),
                                        fontSize = 16.sp
                                    )
                                }
                                val is2FAActive = currentUser?.isTwoFactorEnabled == true
                                Switch(
                                    checked = is2FAActive,
                                    onCheckedChange = { isChecked ->
                                        viewModel.toggle2FA(isChecked)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFFEADDFF),
                                        checkedTrackColor = Color(0xFF21005D),
                                        uncheckedThumbColor = Color(0xFF938F99),
                                        uncheckedTrackColor = Color(0xFF49454F)
                                    ),
                                    modifier = Modifier.scale(0.85f).testTag("security_2fa_switch")
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentUser?.isTwoFactorEnabled == true) {
                                    "Two-Factor Authentication is active for this device."
                                } else {
                                    "Two-Factor Authentication is currently inactive. Secure your system now!"
                                },
                                fontSize = 13.sp,
                                color = Color(0xFF21005D).copy(alpha = 0.82f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (currentUser?.isTwoFactorEnabled == true) {
                                            viewModel.navigateTo(Screen.OTPVerification)
                                        } else {
                                            viewModel.toggle2FA(true)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21005D)),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.weight(1f).height(44.dp).testTag("verify_otp_button")
                                ) {
                                    Text(
                                        text = if (currentUser?.isTwoFactorEnabled == true) "VERIFY OTP" else "ENABLE 2FA",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color(0xFF6750A4), RoundedCornerShape(16.dp))
                                        .clickable {
                                            viewModel.triggerNotification("QR Generator", "Secured pairing key loaded successfully.")
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCode,
                                        contentDescription = "Secured Pairing QR Code",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // C. Mandatory Details Section: divide-y divide-[#49454F] border-[#49454F]
                    Column(modifier = Modifier.padding(bottom = 20.dp)) {
                        Text(
                            text = "MANDATORY DETAILS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF938F99),
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                            letterSpacing = 1.sp
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF49454F), RoundedCornerShape(20.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2930)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column {
                                // Detail 1: Recovery Email
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AlternateEmail,
                                        contentDescription = "Recovery Email Icon",
                                        tint = Color(0xFFD0BCFF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Recovery Email",
                                            fontSize = 11.sp,
                                            color = Color(0xFF938F99)
                                        )
                                        Text(
                                            text = currentUser?.email ?: "admin@system.io",
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Badge",
                                        tint = Color(0xFF938F99),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                
                                HorizontalDivider(color = Color(0xFF49454F))
                                
                                // Detail 2: Linked Phone
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = "Linked Phone Icon",
                                        tint = Color(0xFFD0BCFF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Linked Phone / Contact No",
                                            fontSize = 11.sp,
                                            color = Color(0xFF938F99)
                                        )
                                        Text(
                                            text = currentUser?.contactNo?.let { no ->
                                                if (no.length > 4) "+91 •••• ••" + no.takeLast(2) else "+91 $no"
                                            } ?: "+91 •••• ••89",
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                    }
                                    Icon(
                                        imageVector = if (currentUser?.contactNo.isNullOrEmpty()) Icons.Default.Warning else Icons.Default.Verified,
                                        contentDescription = "Status warning or check",
                                        tint = Color(0xFF938F99),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                if (!currentUser?.gstNo.isNullOrEmpty() || !currentUser?.businessName.isNullOrEmpty()) {
                                    HorizontalDivider(color = Color(0xFF49454F))
                                    
                                    // Detail 3: Registered Business
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Business,
                                            contentDescription = "Business Name Icon",
                                            tint = Color(0xFFD0BCFF),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Registered Corporate Node",
                                                fontSize = 11.sp,
                                                color = Color(0xFF938F99)
                                            )
                                            Text(
                                                text = currentUser?.businessName ?: "Corporate Office",
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Registered Verified",
                                            tint = Color(0xFF938F99),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Category 1: Vehicle Sales (BASIC+)
                if (selectedServiceCategory == "ALL" || selectedServiceCategory == "VEHICLES") {
                    Text(
                        text = "VEHICLE SALES & MOBILITY",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    ServiceListContainer {
                        ServiceItemRow(
                            icon = Icons.Default.DirectionsCar,
                            title = "Passenger Logistics",
                            subtitle = "Sedans, Compacts, SUVs, Premium Hatchbacks",
                            isLocked = false,
                            onClick = {
                                showInquiryDialog = "Passenger Vehicles Catalog"
                                inquirySpec = "Sedans and SUVs"
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.LocalShipping,
                            title = "Commercial Cargo",
                            subtitle = "Pickups, Vans, Transport Trucks, Carriers",
                            isLocked = false,
                            onClick = {
                                showInquiryDialog = "Commercial Cargo Trucks"
                                inquirySpec = "Heavy Haul Vans and Trucks"
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.Beenhere,
                            title = "Pre-Owned Network",
                            subtitle = "Multi-point inspection checked second-hand solutions",
                            isLocked = false,
                            onClick = {
                                showInquiryDialog = "Pre-Owned Certified Vehicles"
                                inquirySpec = "Pre-Inspected Vehicles"
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Category 2: Digital Seva Kendra (BASIC+)
                if (selectedServiceCategory == "ALL" || selectedServiceCategory == "SEVA") {
                    Text(
                        text = "DIGITAL SEVA KENDRA WIDGETS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    ServiceListContainer {
                        ServiceItemRow(
                            icon = Icons.Default.Fingerprint,
                            title = "Aadhaar Demographic portal",
                            subtitle = "Enrolment documentation, Demographic & address corrections",
                            isLocked = false,
                            onClick = {
                                showInquiryDialog = "Aadhaar Card Center"
                                inquirySpec = "Aadhaar Registration Desk"
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.ContactMail,
                            title = "PAN Card Desk",
                            subtitle = "Creation of fresh PAN card allotments & data corrections",
                            isLocked = false,
                            onClick = {
                                showInquiryDialog = "PAN Card Allotment"
                                inquirySpec = "PAN Issuance Panel"
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.BusinessCenter,
                            title = "Udyam Registration Hub",
                            subtitle = "Business legal MSME & small shop registrations",
                            isLocked = false,
                            onClick = {
                                showInquiryDialog = "Udyam MSME Registry"
                                inquirySpec = "MSME Support Certification"
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.HowToVote,
                            title = "Election Voter Registration",
                            subtitle = "New registration schedules, Voter Card corrections",
                            isLocked = false,
                            onClick = {
                                showInquiryDialog = "Voter Election Card Registration"
                                inquirySpec = "Voter Card Correction"
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.FoodBank,
                            title = "Ration Card modifications",
                            subtitle = "Adding / deleting family members and quota changes",
                            isLocked = false,
                            onClick = {
                                showInquiryDialog = "Ration Card Center"
                                inquirySpec = "Ration Card Verification Form"
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Category 3: Vehicle Insurance (PREMIUM+) - LOCKED for Basic
                if (selectedServiceCategory == "ALL" || selectedServiceCategory == "INSURANCE") {
                    Text(
                        text = "VEHICLE INSURANCE GATEWAY",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    ServiceListContainer(
                        borderColor = if (canAccessInsurance) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    ) {
                        ServiceItemRow(
                            icon = Icons.Default.HealthAndSafety,
                            title = "Comprehensive Coverage",
                            subtitle = "Complete financial safeguard against damages & theft",
                            isLocked = !canAccessInsurance,
                            onClick = {
                                if (canAccessInsurance) {
                                    showInquiryDialog = "Comprehensive Insurance Plan"
                                    inquirySpec = "Coverage calculation request"
                                } else {
                                    showUpgradeDialog = true
                                }
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.Shield,
                            title = "Third-Party Liability Desk",
                            subtitle = "Mandatory covers for third-party properties and limits",
                            isLocked = !canAccessInsurance,
                            onClick = {
                                if (canAccessInsurance) {
                                    showInquiryDialog = "Third Party Cover Registry"
                                    inquirySpec = "Mandatory 3rd Party Liability Allotment"
                                } else {
                                    showUpgradeDialog = true
                                }
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.Business,
                            title = "Corporate Fleet Insurance",
                            subtitle = "Special premium limits optimized for commercial groups",
                            isLocked = !canAccessInsurance,
                            onClick = {
                                if (canAccessInsurance) {
                                    showInquiryDialog = "Corporate Fleet Coverage"
                                    inquirySpec = "Industrial vehicle list cover"
                                } else {
                                    showUpgradeDialog = true
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Category 4: IT Services (ENTERPRISE ONLY) - LOCKED for Basic/Premium
                if (selectedServiceCategory == "ALL" || selectedServiceCategory == "IT") {
                    Text(
                        text = "ENTERPRISE IT SOLUTION SERVICES",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    ServiceListContainer(
                        borderColor = if (canAccessIT) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)
                    ) {
                        ServiceItemRow(
                            icon = Icons.Default.DeveloperMode,
                            title = "Custom Software Engineering",
                            subtitle = "Full-stack mobile engines, API hubs, automated scripts",
                            isLocked = !canAccessIT,
                            onClick = {
                                if (canAccessIT) {
                                    showInquiryDialog = "Custom App System Development"
                                    inquirySpec = "Full Stack Platform Deployment"
                                } else {
                                    showUpgradeDialog = true
                                }
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.Terminal,
                            title = "Web System designs",
                            subtitle = "High SEO web pages, interactive design patterns",
                            isLocked = !canAccessIT,
                            onClick = {
                                if (canAccessIT) {
                                    showInquiryDialog = "Professional Custom Website"
                                    inquirySpec = "Tailwind, React, and Android API Sync"
                                } else {
                                    showUpgradeDialog = true
                                }
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.BugReport,
                            title = "Cybersecurity compliance audit",
                            subtitle = "Enterprise database pentesting, vulnerability safeguards",
                            isLocked = !canAccessIT,
                            onClick = {
                                if (canAccessIT) {
                                    showInquiryDialog = "Cyber Compliance Check"
                                    inquirySpec = "Network security assessment"
                                } else {
                                    showUpgradeDialog = true
                                }
                            }
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        ServiceItemRow(
                            icon = Icons.Default.CloudSync,
                            title = "Cloud deployments Estimators",
                            subtitle = "Secure GCP, AWS server migrations, Kubernetes clusters",
                            isLocked = !canAccessIT,
                            onClick = {
                                if (canAccessIT) {
                                    showInquiryDialog = "DevOps Cloud Setup"
                                    inquirySpec = "Scale configurations estimate"
                                } else {
                                    showUpgradeDialog = true
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Banner Footer Info matching Image Bottom Line exactly
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = adminConfig.infoFooter,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
            }
            
            // Sleek Custom Bottom Navigation
            SleekBottomNav(
                currentScreen = Screen.MainDashboard,
                onNavigate = { viewModel.navigateTo(it) },
                onPlansClick = { showUpgradeDialog = true },
                isAdmin = currentUser?.role == "Admin"
            )
        }

        // UPGRADE PLANS DIALOG POPUP
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

        // INTERACTIVE INQUIRY FORM DIALOG
        showInquiryDialog?.let { title ->
            AlertDialog(
                onDismissRequest = { showInquiryDialog = null },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Inquiry Logo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Request Support: $title",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            fontSize = 16.sp
                        )
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Submit a request to Nikunj & Chirag direct team. Only admins can inspect this data.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = inquiryName,
                            onValueChange = { inquiryName = it },
                            label = { Text("Your Business Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = inquiryMessage,
                            onValueChange = { inquiryMessage = it },
                            label = { Text("Explain requirements or details") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            maxLines = 3,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            // Mock submit inquiry by adding custom GST/Business changes
                            viewModel.tempPendingUser.value = currentUser // reuse temp state
                            currentUser?.let { user ->
                                // Save request details directly to local Room database via ViewModel! Correct & clean
                                viewModel.saveMandatoryDetails(
                                    contact = user.contactNo,
                                    gst = user.gstNo,
                                    businessName = inquiryName.ifBlank { user.businessName },
                                    address = "Inquiry: [${inquiryMessage.ifBlank { "Support Details Requested" }}] | Address: " + user.address
                                )
                            }
                            showInquiryDialog = null
                            inquiryName = ""
                            inquiryMessage = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Deploy Request", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showInquiryDialog = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun ServiceListContainer(
    borderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
        content = content
    )
}

@Composable
fun ServiceItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
            .testTag("active_feature_card"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (isLocked) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLocked) Icons.Default.Lock else icon,
                contentDescription = "$title Icon",
                tint = if (isLocked) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isLocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (isLocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
            )
        }

        if (isLocked) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "GOLD LOCK",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Enter Feature Button",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SleekBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onPlansClick: () -> Unit,
    isAdmin: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color(0xFF2B2930),
        border = BorderStroke(width = 1.dp, color = Color(0xFF49454F).copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home / Dashboard
            val homeActive = currentScreen == Screen.MainDashboard
            Column(
                modifier = Modifier
                    .clickable { onNavigate(Screen.MainDashboard) }
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (homeActive) Color(0xFF4F378B) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home Navigation",
                        tint = if (homeActive) Color(0xFFD0BCFF) else Color(0xFF938F99).copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Home",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (homeActive) Color(0xFFD0BCFF) else Color(0xFF938F99).copy(alpha = 0.8f)
                )
            }

            // Plans
            Column(
                modifier = Modifier
                    .clickable { onPlansClick() }
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(color = Color.Transparent, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Plans Subscription Navigation",
                        tint = Color(0xFF938F99).copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Plans",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF938F99).copy(alpha = 0.8f)
                )
            }

            // Admin Panel (Visible if user is indeed Admin)
            if (isAdmin) {
                val adminActive = currentScreen == Screen.AdminPanel
                Column(
                    modifier = Modifier
                        .clickable { onNavigate(Screen.AdminPanel) }
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (adminActive) Color(0xFF4F378B) else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 18.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Navigation",
                            tint = if (adminActive) Color(0xFFD0BCFF) else Color(0xFF938F99).copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Admin",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (adminActive) Color(0xFFD0BCFF) else Color(0xFF938F99).copy(alpha = 0.8f)
                    )
                }
            }

            // Setup / Settings
            val settingsActive = currentScreen == Screen.Settings
            Column(
                modifier = Modifier
                    .clickable { onNavigate(Screen.Settings) }
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (settingsActive) Color(0xFF4F378B) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Setup Navigation",
                        tint = if (settingsActive) Color(0xFFD0BCFF) else Color(0xFF938F99).copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Setup",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (settingsActive) Color(0xFFD0BCFF) else Color(0xFF938F99).copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun UpgradeChoiceCard(
    title: String,
    desc: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onClick)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}


