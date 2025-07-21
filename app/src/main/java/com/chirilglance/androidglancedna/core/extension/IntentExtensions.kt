package com.chirilglance.androidglancedna.core.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

/**
 * Extensions for launching external apps and intents from the app.
 * These functions provide convenient ways to perform common external actions.
 */

/**
 * Open a URL in Chrome Custom Tabs for a better user experience.
 * Falls back to the default browser if Custom Tabs is not available.
 *
 * @param url The URL to open
 */
fun Context.openUrlInCustomTabs(url: String) {
    try {
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
            .setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
            .build()
        intent.launchUrl(this, url.toUri())
    } catch (e: Exception) {
        // Fallback to default browser if Custom Tabs is not available
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No browser app found", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * Open the default email app with pre-filled recipient, subject, and body.
 *
 * @param email The recipient email address
 * @param subject Optional email subject
 * @param body Optional email body
 */
fun Context.openEmailApp(email: String, subject: String? = null, body: String? = null) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        if (subject != null) {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        if (body != null) {
            putExtra(Intent.EXTRA_TEXT, body)
        }
    }

    // Check if there's an app that can handle this intent
    if (intent.resolveActivity(packageManager) != null) {
        // Create a chooser to let user select which email app to use
        val chooser = Intent.createChooser(intent, "Send email using...")
        startActivity(chooser)
    } else {
        // Handle case where no email app is available
        Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Open the phone dialer with a pre-filled phone number.
 *
 * @param phoneNumber The phone number to dial
 */
fun Context.openPhoneDialer(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "No phone app found", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Open maps application with the specified location.
 *
 * @param latitude The latitude coordinate
 * @param longitude The longitude coordinate
 * @param label Optional label for the location
 */
fun Context.openMapsWithLocation(latitude: Double, longitude: Double, label: String? = null) {
    val uriString = if (label != null) {
        "geo:$latitude,$longitude?q=$latitude,$longitude($label)"
    } else {
        "geo:$latitude,$longitude"
    }

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(this, "No maps app found", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Share text content with other apps.
 *
 * @param title The title for the share sheet
 * @param content The text content to share
 */
fun Context.shareText(title: String, content: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, content)
    }

    val chooser = Intent.createChooser(intent, "Share via")
    startActivity(chooser)
}

/**
 * Open the app's notification settings in the system settings.
 */
fun Context.openAppNotificationSettings() {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    }
    try {
        startActivity(intent)
    } catch (e: Exception) {
        // Fallback to general settings if the specific intent fails
        val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
        startActivity(fallbackIntent)
    }
}

/**
 * Open app details in system settings.
 * Useful for directing users to app permissions.
 */
fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}

/**
 * Open the device's location settings.
 * Useful when the app needs location permissions.
 */
fun Context.openLocationSettings() {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    startActivity(intent)
}

/**
 * Open a specific app on the Play Store.
 *
 * @param packageNameOrLink The package name or full link of the app to open
 * @param fallbackToWeb Whether to fall back to the web Play Store if the app is not installed
 */
fun Context.openPlayStore(
    packageNameOrLink: String = this.packageName,
    fallbackToWeb: Boolean = true
) {
    // Extract package name if a full link was provided
    val packageName = if (packageNameOrLink.startsWith("http")) {
        val uri = Uri.parse(packageNameOrLink)
        uri.getQueryParameter("id") ?: packageNameOrLink
    } else {
        packageNameOrLink
    }

    try {
        // Try to open in Play Store app
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=$packageName")
        }
        startActivity(intent)
    } catch (e: Exception) {
        if (fallbackToWeb) {
            // Fall back to web browser
            val webIntent = Intent(Intent.ACTION_VIEW).apply {
                // If the original input was a URL, use that directly
                data = if (packageNameOrLink.startsWith("http")) {
                    Uri.parse(packageNameOrLink)
                } else {
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                }
            }
            startActivity(webIntent)
        } else {
            Toast.makeText(this, "Play Store app not found", Toast.LENGTH_SHORT).show()
        }
    }
}