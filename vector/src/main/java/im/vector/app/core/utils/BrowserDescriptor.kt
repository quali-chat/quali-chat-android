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

package im.vector.app.core.utils

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.util.Base64
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

data class BrowserDescriptor(
        val packageName: String,
        val signatureHashes: Set<String>,
        val version: String,
        val useCustomTab: Boolean
) {
    companion object {
        private const val PRIME_HASH_FACTOR = 92821
        private const val DIGEST_SHA_512 = "SHA-512"

        fun generateSignatureHash(signature: Signature): String {
            return try {
                val digest = MessageDigest.getInstance(DIGEST_SHA_512)
                val hashBytes = digest.digest(signature.toByteArray())
                Base64.encodeToString(hashBytes, Base64.URL_SAFE or Base64.NO_WRAP)
            } catch (e: NoSuchAlgorithmException) {
                throw IllegalStateException("Platform does not support $DIGEST_SHA_512 hashing")
            }
        }

        fun generateSignatureHashes(signatures: Array<Signature>): Set<String> {
            return signatures.mapTo(mutableSetOf()) { generateSignatureHash(it) }
        }
    }
    @Suppress("DEPRECATION")
    constructor(packageInfo: PackageInfo, useCustomTab: Boolean) : this(
            packageInfo.packageName,
            generateSignatureHashes(packageInfo.signatures),
            packageInfo.versionName,
            useCustomTab
    )

    fun changeUseCustomTab(newUseCustomTabValue: Boolean): BrowserDescriptor {
        return copy(useCustomTab = newUseCustomTabValue)
    }

    override fun hashCode(): Int {
        var hash = packageName.hashCode()
        hash = PRIME_HASH_FACTOR * hash + version.hashCode()
        hash = PRIME_HASH_FACTOR * hash + if (useCustomTab) 1 else 0
        for (signatureHash in signatureHashes) {
            hash = PRIME_HASH_FACTOR * hash + signatureHash.hashCode()
        }
        return hash
    }
}
