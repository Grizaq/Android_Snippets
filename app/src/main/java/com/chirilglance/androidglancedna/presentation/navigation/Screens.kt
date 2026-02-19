package com.chirilglance.androidglancedna.presentation.navigation

sealed class Screen(val route: String) {
    // Base route without parameters
    val baseRoute: String
        get() = route.split("/")[0]

    // For navigation with parameters
    open fun createRoute(vararg params: String): String = route

    // Authentication screens
    data object PhoneVerification : Screen("phone_verification")
    data object OtpVerification : Screen("otp_verification/{phoneNumber}/{verificationId}") {
        override fun createRoute(vararg params: String): String =
            "otp_verification/${params[0]}/${params[1]}"
    }
    data object Welcome : Screen("welcome")

    // Main app screens
    data object Home : Screen("home")
    data object UiComponents : Screen("ui_components")
    data object FormValidation : Screen("form_validation")
    data object Authentication : Screen("authentication")
    data object EmailSignIn : Screen("email_sign_in")
    data object LocationServices : Screen("location_services")
    data object StateManagement : Screen("state_management")

    // Profile Management screens
    data object ProfileManagement : Screen("profile_management")
    data object ProfileDetails : Screen("profile_details/{profileId}") {
        override fun createRoute(vararg params: String): String =
            "profile_details/${params[0]}"
    }
    data object ProfileEdit : Screen("profile_edit/{profileId}") {
        override fun createRoute(vararg params: String): String =
            "profile_edit/${params[0]}"
    }
}