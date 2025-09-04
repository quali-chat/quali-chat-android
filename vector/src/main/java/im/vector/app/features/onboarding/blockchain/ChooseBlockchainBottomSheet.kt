/*
 * Copyright (c) 2025 Keypair Establishment
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

package im.vector.app.features.onboarding.blockchain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import im.vector.app.R
import im.vector.app.databinding.BottomSheetChooseBlockchainBinding
import im.vector.app.features.onboarding.ftueauth.FtueAuthSignUpSignInSelectionFragment

class ChooseBlockchainBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetChooseBlockchainBinding
    private lateinit var callback: (BlockchainOption?) -> Unit
    private lateinit var demoLoginCallback: () -> Unit

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetChooseBlockchainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeButton.setOnClickListener {
            dismiss()
        }
        displaySSO()

        binding.demoSubmit.setOnClickListener {
            demoLoginCallback()
            dismiss()
        }
    }

    private fun displaySSO() {
        val fragment = FtueAuthSignUpSignInSelectionFragment()
        fragment.setCallback {
            dismiss()
        }
        childFragmentManager.beginTransaction().apply {
            replace(R.id.placeHolderBottomSheet, fragment)
            commit()
        }
    }

    fun setCallback(callback: (BlockchainOption?) -> Unit, demoLoginCallback: () -> Unit) {
        this.callback = callback
        this.demoLoginCallback = demoLoginCallback
    }
}
