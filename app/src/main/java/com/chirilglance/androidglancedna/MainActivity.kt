package com.chirilglance.androidglancedna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.chirilglance.androidglancedna.navigation.AppNavigation
import com.chirilglance.androidglancedna.ui.theme.AndroidGlanceDNATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidGlanceDNATheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}