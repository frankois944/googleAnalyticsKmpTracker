@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker.user

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import io.github.frankois944.googleAnalyticsKMPTracker.context.ContextObject.context
import java.util.Locale

internal actual object Device {

    actual val isBrowser: Boolean = false
    actual val model: String = "Android"
    actual val operatingSystem: String = Build.MODEL ?: "Android"
    actual val osVersion: String = Build.VERSION.RELEASE ?: "0"
    actual val screenSize: Size =
        getResolution()?.let {
            Size(it[0].toLong(), it[1].toLong())
        } ?: Size(0, 0)
    actual val nativeScreenSize: Size? = null
    actual val softwareId: String? = null
    actual val language: String?
        get() = Locale.getDefault().language + "-" + Locale.getDefault().country

    actual val identifier: String? = context?.get()?.packageName

    fun getResolution(): IntArray? {
        context?.get()?.apply {
            var width: Int
            var height: Int
            val display: Display
            try {
                val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                display = wm.defaultDisplay
            } catch (e: NullPointerException) {
                return null
            }

            // Recommended way to get the resolution but only available since API17
            val displayMetrics = DisplayMetrics()
            display.getRealMetrics(displayMetrics)
            width = displayMetrics.widthPixels
            height = displayMetrics.heightPixels

            if (width == -1 || height == -1) {
                // This is not accurate on all 4.2+ devices, usually the height is wrong due to statusbar/softkeys
                // Better than nothing though.
                val dm = DisplayMetrics()
                display.getMetrics(dm)
                width = dm.widthPixels
                height = dm.heightPixels
            }

            return intArrayOf(width, height)
        }
        return null
    }

    actual val category: String
        get() {
            context?.get()?.apply {
                val packageManager = this.packageManager
                // Check for Android TV
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
                    return "smart TV"
                }

                // Check for Wear OS (smartwatch)
                val config: Configuration = resources.configuration
                val isWatch = (config.uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_WATCH
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH) || isWatch) {
                    return "smart Watch"
                }
                // Default to mobile for phones and tablets
                return "mobile"
            }
            // Fallback if context is null
            return "mobile"
        }
    actual val browser: String? = operatingSystem
    actual val browserVersion: String? = null
    actual val currentUserAgent: String? = null
    actual val brand: String = Build.BRAND
}
