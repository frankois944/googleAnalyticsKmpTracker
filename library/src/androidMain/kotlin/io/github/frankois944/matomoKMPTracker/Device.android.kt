@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import io.github.frankois944.matomoKMPTracker.context.ContextObject.context
import java.util.Locale

internal actual class Device {
    actual val model: String = "Android"
    actual val operatingSystem: String
        get() = Build.MODEL ?: "Android"
    actual val osVersion: String
        get() = Build.VERSION.RELEASE ?: "0"
    actual val screenSize: Size
        get() =
            getResolution()?.let {
                Size(it[0].toLong(), it[1].toLong())
            } ?: Size(0, 0)
    actual val nativeScreenSize: Size?
        get() = null
    actual val softwareId: String?
        get() = null
    actual val language: String?
        get() = Locale.getDefault().language + "-" + Locale.getDefault().country

    actual val actionUrl: String?
        get() = context?.get()?.packageName

    actual companion object Builder {
        actual fun create(): Device = Device()
    }

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
}
