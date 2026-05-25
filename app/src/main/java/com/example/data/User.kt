package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val username: String,
    val passwordHash: String,
    val email: String,
    val planName: String = "Basic", // "Basic", "Premium", "Enterprise"
    val isTwoFactorEnabled: Boolean = false,
    val secretOtpKey: String = "123456", // Mock OTP secret or static for simple mock verification
    val mandatoryDetailsFilled: Boolean = false,
    val contactNo: String = "",
    val gstNo: String = "",
    val businessName: String = "",
    val address: String = "",
    val role: String = "User" // "User" or "Admin"
)

@Entity(tableName = "admin_config")
data class AdminConfig(
    @PrimaryKey val id: Int = 1,
    val contactNo: String = "+91 6353161655",
    val altContactNo: String = "+91 8200782140",
    val email: String = "nctechandservices@gmail.com",
    val supportEmail: String = "support@nctech.com",
    val appHeadline: String = "NC TECH HUB",
    val appSubHeadline: String = "TECHNOLOGY & MOBILITY SOLUTIONS",
    val infoFooter: String = "BY NIKUNJ & CHIRAG | INNOVATIVE & DIRECT SOLUTIONS",
    val adminAvatarType: String = "avatar_boss", // "avatar_boss", "avatar_agent", "avatar_guru", "avatar_shield", "avatar_custom"
    val adminCustomAvatarUri: String = "",
    val customAnnouncement: String = "Welcome to NC Tech Hub! Explore our premium Technology and Mobility solutions."
)
