package com.example.demosih11.model

object RegistrationCache {

    data class UserRegistration(
        var role: String = "",
        var firstName: String = "",
        var lastName: String = "",
        var phone: String = "",
        var email: String = "",
        var aadharNo: String = "",
        var gstNo: String = "",
        var aadharPhotoUrl: String? = null,
        var isVerified: Boolean = false,
        var uid: String = ""
    )
    val pendingApplications = mutableListOf<UserRegistration>(
        // Seed default application entries for demonstration purposes
        UserRegistration("Kabadi Wala", "Rajesh", "Kumar", "9876543210", "rajesh@kabadi.com", "5241-8963-7412", "07AAAAA1111A1Z1"),
        UserRegistration("Recycler", "Amit", "Sharma", "9911223344", "contact@greentech.in", "4125-9632-8521", "09BBBBB2222B1Z2")
    )
}