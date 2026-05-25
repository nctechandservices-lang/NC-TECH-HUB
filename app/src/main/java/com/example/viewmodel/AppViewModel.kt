package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AdminConfig
import com.example.data.AppDatabase
import com.example.data.User
import com.example.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class Screen {
    Login,
    Register,
    OTPVerification,
    MainDashboard,
    AdminPanel,
    Settings,
    FillMandatoryDetails
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    // Session flows
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Screen navigation flow
    private val _currentScreen = MutableStateFlow(Screen.Login)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Theme state ("System", "Light", "Dark")
    private val _themeMode = MutableStateFlow("Dark") // default dark for great tech aesthetic
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // Two-factor OTP configurations
    private val _generatedOtp = MutableStateFlow("")
    val generatedOtp: StateFlow<String> = _generatedOtp.asStateFlow()

    private val _otpPurpose = MutableStateFlow("Login") // "Login", "Register", "Toggle2FA"
    val otpPurpose: StateFlow<String> = _otpPurpose.asStateFlow()

    // Temp cache for registering user before complete OTP validation
    val tempPendingUser = MutableStateFlow<User?>(null)

    // User alerts / notifications
    private val _otpToastMessage = MutableStateFlow<String?>(null)
    val otpToastMessage: StateFlow<String?> = _otpToastMessage.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = UserRepository(database.userDao())
    }

    // Flows connected directly to the database for reactive updates
    val allUsers: StateFlow<List<User>> = repository.allItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val adminConfig: StateFlow<AdminConfig> = repository.adminConfigFlow
        .map { it ?: AdminConfig() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AdminConfig()
        )

    // Helper property to map database item flow as required by skill guidelines
    private companion object {
        val UserRepository.allItems: kotlinx.coroutines.flow.Flow<List<User>>
            get() = this.allUsers
    }

    fun setTheme(mode: String) {
        _themeMode.value = mode
    }

    fun dismissAuthError() {
        _authError.value = null
    }

    fun dismissOtpToast() {
        _otpToastMessage.value = null
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // Trigger secure simulated OTP validation
    private fun triggerOtp(purpose: String) {
        val code = (100000..999999).random().toString()
        _generatedOtp.value = code
        _otpPurpose.value = purpose
        _otpToastMessage.value = "🔒 NC TECH AUTH: Your Security Verification Pin is $code"
        _currentScreen.value = Screen.OTPVerification
    }

    // Safely resends simulated OTP code
    fun resendOtpCode() {
        if (tempPendingUser.value != null) {
            triggerOtp(_otpPurpose.value)
        }
    }

    // Perform verification checks during login
    fun login(usernameInput: String, passwordInput: String) {
        viewModelScope.launch {
            val user = repository.getUserByUsername(usernameInput.trim())
            if (user == null) {
                _authError.value = "Username not found. Please register or contact system administrator."
                return@launch
            }
            if (user.passwordHash != passwordInput) {
                _authError.value = "Invalid credentials. Please enter your registered credentials."
                return@launch
            }

            // If user credentials match, check for active 2FA parameters
            if (user.isTwoFactorEnabled) {
                tempPendingUser.value = user
                triggerOtp("Login")
            } else {
                completeUserLogin(user)
            }
        }
    }

    // Execute logged-in configuration checks
    private fun completeUserLogin(user: User) {
        _currentUser.value = user
        if (user.role == "Admin") {
            _currentScreen.value = Screen.AdminPanel
        } else if (!user.mandatoryDetailsFilled) {
            _currentScreen.value = Screen.FillMandatoryDetails
        } else {
            _currentScreen.value = Screen.MainDashboard
        }
    }

    // Complete register setup
    fun register(
        usernameInput: String,
        emailInput: String,
        passwordInput: String,
        selectedPlan: String,
        enableTwoFactor: Boolean
    ) {
        viewModelScope.launch {
            val existing = repository.getUserByUsername(usernameInput.trim())
            if (existing != null) {
                _authError.value = "Username already taken! Please choose another."
                return@launch
            }

            val newUser = User(
                username = usernameInput.trim(),
                email = emailInput.trim(),
                passwordHash = passwordInput,
                planName = selectedPlan,
                isTwoFactorEnabled = enableTwoFactor,
                mandatoryDetailsFilled = false, // user will fill this immediately
                role = "User"
            )

            if (enableTwoFactor) {
                tempPendingUser.value = newUser
                triggerOtp("Register")
            } else {
                val newId = repository.insertUser(newUser)
                val loggedUser = newUser.copy(id = newId)
                completeUserLogin(loggedUser)
            }
        }
    }

    // OTP Verify action
    fun verifyOtp(inputCode: String): Boolean {
        if (inputCode == _generatedOtp.value) {
            viewModelScope.launch {
                val purpose = _otpPurpose.value
                val user = tempPendingUser.value
                if (user != null) {
                    if (purpose == "Register") {
                        val newId = repository.insertUser(user)
                        completeUserLogin(user.copy(id = newId))
                    } else if (purpose == "Toggle2FA") {
                        // User toggling 2FA inside profile dashboard
                        val updatedUser = user.copy(isTwoFactorEnabled = true)
                        repository.updateUser(updatedUser)
                        _currentUser.value = updatedUser
                        _currentScreen.value = Screen.Settings
                    } else {
                        // Regular login verified
                        completeUserLogin(user)
                    }
                    tempPendingUser.value = null
                    _otpToastMessage.value = "✅ Multi-Factor Authentication Approved!"
                }
            }
            return true
        } else {
            return false
        }
    }

    // Update mandatory client profile parameters
    fun saveMandatoryDetails(
        contact: String,
        gst: String,
        businessName: String,
        address: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updatedUser = user.copy(
                contactNo = contact.trim(),
                gstNo = gst.trim().uppercase(),
                businessName = businessName.trim(),
                address = address.trim(),
                mandatoryDetailsFilled = true
            )
            repository.updateUser(updatedUser)
            _currentUser.value = updatedUser
            _currentScreen.value = Screen.MainDashboard
        }
    }

    // Update user sub plan from billing displays
    fun purchasePlan(planName: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updatedUser = user.copy(planName = planName)
            repository.updateUser(updatedUser)
            _currentUser.value = updatedUser
            _otpToastMessage.value = "🎉 Plan upgraded to $planName successfully!"
        }
    }

    // Toggle 2FA in settings
    fun toggle2FA(enabled: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            if (enabled) {
                // Generate OTP confirmation challenge
                tempPendingUser.value = user
                triggerOtp("Toggle2FA")
            } else {
                val updatedUser = user.copy(isTwoFactorEnabled = false)
                repository.updateUser(updatedUser)
                _currentUser.value = updatedUser
                _otpToastMessage.value = "🔓 2FA has been disabled"
            }
        }
    }

    // Action function for administrative updates
    fun updateAdminConfig(
        contact: String,
        altContact: String,
        email: String,
        supportEmail: String,
        headline: String,
        subHeadline: String,
        footer: String,
        avatarType: String,
        customAvatarUri: String,
        announcement: String
    ) {
        viewModelScope.launch {
            val newConfig = AdminConfig(
                id = 1,
                contactNo = contact.trim(),
                altContactNo = altContact.trim(),
                email = email.trim(),
                supportEmail = supportEmail.trim(),
                appHeadline = headline.trim(),
                appSubHeadline = subHeadline.trim(),
                infoFooter = footer.trim(),
                adminAvatarType = avatarType,
                adminCustomAvatarUri = customAvatarUri.trim(),
                customAnnouncement = announcement.trim()
            )
            repository.saveAdminConfig(newConfig)
            _otpToastMessage.value = "🛡️ NC TECH HUB Configs successfully deployed by Admin!"
            
            // Also keep current logged admin sync with contact changes
            val current = _currentUser.value
            if (current != null && current.role == "Admin") {
                val updatedAdmin = current.copy(
                    contactNo = contact,
                    businessName = headline,
                    address = footer
                )
                repository.updateUser(updatedAdmin)
                _currentUser.value = updatedAdmin
            }
        }
    }

    // Trigger simulated in-app notification alerts
    fun triggerNotification(title: String, message: String) {
        _otpToastMessage.value = "🔔 $title: $message"
    }

    // Log the current user session out
    fun logout() {
        _currentUser.value = null
        tempPendingUser.value = null
        _currentScreen.value = Screen.Login
    }
}
