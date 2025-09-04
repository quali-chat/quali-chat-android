/*
 * Copyright (c) 2025 Keypair Establishment
 * Copyright 2015 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.VisibleForTesting
import androidx.browser.customtabs.CustomTabsService
import im.vector.app.core.utils.BrowserDescriptor
import im.vector.app.core.utils.BrowserMatcher

object BrowserSelector {
    private const val SCHEME_HTTP = "http"
    private const val SCHEME_HTTPS = "https"

    @VisibleForTesting
    val ACTION_CUSTOM_TABS_CONNECTION: String = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION

    @VisibleForTesting
    val BROWSER_INTENT: Intent = Intent().apply {
        action = Intent.ACTION_VIEW
        addCategory(Intent.CATEGORY_BROWSABLE)
        data = Uri.fromParts(SCHEME_HTTP, "", null)
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun getAllBrowsers(context: Context): List<BrowserDescriptor> {
        val pm: PackageManager = context.packageManager
        val browsers = mutableListOf<BrowserDescriptor>()
        val defaultBrowserPackage: String?

        var queryFlag = PackageManager.GET_RESOLVED_FILTER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            queryFlag = queryFlag or PackageManager.MATCH_ALL
        }

        val resolvedDefaultActivity = pm.resolveActivity(BROWSER_INTENT, 0)
        defaultBrowserPackage = resolvedDefaultActivity?.activityInfo?.packageName

        val resolvedActivityList = pm.queryIntentActivities(BROWSER_INTENT, queryFlag)
        for (info in resolvedActivityList) {
            if (!isFullBrowser(info)) continue

            try {
                @Suppress("DEPRECATION")
                val packageInfo = pm.getPackageInfo(info.activityInfo.packageName, PackageManager.GET_SIGNATURES)
                var defaultBrowserIndex = 0

                if (hasWarmupService(pm, info.activityInfo.packageName)) {
                    val customTabBrowserDescriptor = BrowserDescriptor(packageInfo, true)
                    if (info.activityInfo.packageName == defaultBrowserPackage) {
                        browsers.add(defaultBrowserIndex, customTabBrowserDescriptor)
                        defaultBrowserIndex++
                    } else {
                        browsers.add(customTabBrowserDescriptor)
                    }
                }

                val fullBrowserDescriptor = BrowserDescriptor(packageInfo, false)
                if (info.activityInfo.packageName == defaultBrowserPackage) {
                    browsers.add(defaultBrowserIndex, fullBrowserDescriptor)
                } else {
                    browsers.add(fullBrowserDescriptor)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                // a descriptor cannot be generated without the package info
            }
        }
        return browsers
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun select(context: Context, browserMatcher: BrowserMatcher): BrowserDescriptor? {
        val allBrowsers = getAllBrowsers(context)
        var bestMatch: BrowserDescriptor? = null

        for (browser in allBrowsers) {
            if (!browserMatcher.matches(browser)) continue

            if (browser.useCustomTab) {
                bestMatch = browser
                break
            }
        }
        return bestMatch
    }

    private fun hasWarmupService(pm: PackageManager, packageName: String): Boolean {
        val serviceIntent = Intent().apply {
            action = ACTION_CUSTOM_TABS_CONNECTION
            setPackage(packageName)
        }
        return pm.resolveService(serviceIntent, 0) != null
    }

    private fun isFullBrowser(resolveInfo: ResolveInfo): Boolean {
        val filter = resolveInfo.filter ?: return false

        if (!filter.hasAction(Intent.ACTION_VIEW) || !filter.hasCategory(Intent.CATEGORY_BROWSABLE) || filter.schemesIterator() == null) {
            return false
        }

        if (filter.authoritiesIterator() != null) {
            return false
        }

        var supportsHttp = false
        var supportsHttps = false
        val schemeIter = filter.schemesIterator()
        while (schemeIter.hasNext()) {
            val scheme = schemeIter.next()
            supportsHttp = supportsHttp || SCHEME_HTTP == scheme
            supportsHttps = supportsHttps || SCHEME_HTTPS == scheme
            if (supportsHttp && supportsHttps) return true
        }

        return false
    }
}
