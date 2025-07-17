package com.chirilglance.androidglancedna.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.chirilglance.androidglancedna.core.error.CrashlyticsManager
import com.chirilglance.androidglancedna.core.ui.scaffold.GlanceAppScaffold
import com.chirilglance.androidglancedna.domain.auth.TokenManager
import com.chirilglance.androidglancedna.domain.profile.ProfileManager
import com.chirilglance.androidglancedna.presentation.navigation.AppNavigation
import com.chirilglance.androidglancedna.presentation.ui.theme.AndroidGlanceDNATheme
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var profileManager: ProfileManager

    @Inject
    lateinit var crashlyticsManager: CrashlyticsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure Firebase is initialized before any Firebase services are used
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to initialize Firebase: ${e.message}", e)
        }

        // Handle session expired flag if coming from MainApplication
        if (intent?.getBooleanExtra("SESSION_EXPIRED", false) == true) {
            lifecycleScope.launch {
                tokenManager.clearToken()
            }
        }

        setContent {
            AndroidGlanceDNATheme {
                val navController = rememberNavController()

                // Set user info in Crashlytics when profile changes
                LaunchedEffect(Unit) {
                    try {
                        profileManager.getActiveProfile()?.let { profile ->
                            crashlyticsManager.setUserInfo(
                                userId = profile.profileId,
                                userProfile = profile
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error setting user info: ${e.message}", e)
                    }
                }

                GlanceAppScaffold(
                    navController = navController
                ) {
                    AppNavigation(navController)
                }
            }
        }
    }
}