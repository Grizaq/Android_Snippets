package com.chirilglance.androidglancedna.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.chirilglance.androidglancedna.core.ui.scaffold.GlanceAppScaffold
import com.chirilglance.androidglancedna.presentation.navigation.AppNavigation
import com.chirilglance.androidglancedna.presentation.ui.theme.AndroidGlanceDNATheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidGlanceDNATheme {
                val navController = rememberNavController()
                GlanceAppScaffold(
                    navController = navController
                ) {
                    AppNavigation(navController)
                }
            }
        }
    }
}