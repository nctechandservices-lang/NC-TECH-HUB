package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import com.example.ui.AdminScreen
import com.example.ui.DashboardScreen
import com.example.ui.FillMandatoryDetailsScreen
import com.example.ui.LoginScreen
import com.example.ui.OTPScreen
import com.example.ui.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.Screen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this)[AppViewModel::class.java]

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val otpToastMessage by viewModel.otpToastMessage.collectAsState()

            MyApplicationTheme(themeMode = themeMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Animated Screen Switching Container
                        AnimatedContent(
                            targetState = currentScreen,
                            label = "screen_routing"
                        ) { screen ->
                            when (screen) {
                                Screen.Login, Screen.Register -> LoginScreen(viewModel)
                                Screen.OTPVerification -> OTPScreen(viewModel)
                                Screen.FillMandatoryDetails -> FillMandatoryDetailsScreen(viewModel)
                                Screen.MainDashboard -> DashboardScreen(viewModel)
                                Screen.AdminPanel -> AdminScreen(viewModel)
                                Screen.Settings -> SettingsScreen(viewModel)
                            }
                        }

                        // Simulated Floating Push Notification SMS Header
                        AnimatedVisibility(
                            visible = otpToastMessage != null,
                            enter = slideInVertically { -it } + fadeIn(),
                            exit = slideOutVertically { -it } + fadeOut(),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(16.dp)
                                .zIndex(99f)
                        ) {
                            otpToastMessage?.let { msg ->
                                LaunchedEffect(msg) {
                                    delay(9000) // Autohide simulated push bar after 9 seconds of in-activity
                                    if (viewModel.otpToastMessage.value == msg) {
                                        viewModel.dismissOtpToast()
                                    }
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .widthIn(max = 480.dp)
                                        .testTag("in_app_notification_banner"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = "SMS Notification Alert",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "Simulated Push SMS",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                text = msg,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                        IconButton(onClick = { viewModel.dismissOtpToast() }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close push toast panel",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
