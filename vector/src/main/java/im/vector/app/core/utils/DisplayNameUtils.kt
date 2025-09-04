/*
 * Copyright (c) 2025 Keypair Establishment
 * Copyright (c) 2024 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.core.utils

import android.widget.TextView
import im.vector.app.core.utils.DisplayNameUtils.BLANK

object DisplayNameUtils {

    const val TG_TAG_SPACED = "[TG] "
    const val TG_TAG = "[TG]"
    const val TG_UNDERSCORE_TAG = "#TG_"
    const val BLANK = ""
    const val SPACE = " "
}

fun String?.removeTgTag(): String {
    if (this == null) {
        return ""
    }

    return replaceFirst(DisplayNameUtils.TG_TAG_SPACED, BLANK)
            .replaceFirst(DisplayNameUtils.TG_TAG, BLANK)
            .replaceFirst(DisplayNameUtils.TG_UNDERSCORE_TAG, BLANK)
}

fun TextView.addChatIcon() {
    setCompoundDrawablesRelativeWithIntrinsicBounds(im.vector.app.R.drawable.ic_ethereum, 0, 0, 0)
    compoundDrawablePadding = 10
}

fun TextView.removeChatIcon() {
    setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
    compoundDrawablePadding = 0
}

fun TextView.swapToEthereumDisplayName(displayName: String?) {
    if (displayName.isNullOrEmpty()) {
        return
    }

    if (displayName.startsWith(DisplayNameUtils.TG_TAG)) {
        text = displayName.removeTgTag()
        this.addChatIcon()
        return
    }

    if (displayName.startsWith(DisplayNameUtils.TG_UNDERSCORE_TAG)) {
        text = displayName.removeTgTag()
        return
    }

    text = displayName
    this.removeChatIcon()
}
