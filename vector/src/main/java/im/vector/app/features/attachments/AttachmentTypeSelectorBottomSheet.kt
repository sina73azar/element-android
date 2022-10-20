/*
 * Copyright (c) 2022 New Vector Ltd
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

package im.vector.app.features.attachments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.parentFragmentViewModel
import com.airbnb.mvrx.withState
import dagger.hilt.android.AndroidEntryPoint
import im.vector.app.core.platform.VectorBaseBottomSheetDialogFragment
import im.vector.app.databinding.BottomSheetAttachmentTypeSelectorBinding
import im.vector.app.features.home.room.detail.TimelineViewModel

@AndroidEntryPoint
class AttachmentTypeSelectorBottomSheet : VectorBaseBottomSheetDialogFragment<BottomSheetAttachmentTypeSelectorBinding>() {

    private val viewModel: AttachmentTypeSelectorViewModel by fragmentViewModel()
    private val timelineViewModel: TimelineViewModel by parentFragmentViewModel()
    private val sharedActionViewModel: AttachmentTypeSelectorSharedActionViewModel by viewModels(
            ownerProducer = { requireParentFragment() }
    )

    override val showExpanded = true

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): BottomSheetAttachmentTypeSelectorBinding {
        return BottomSheetAttachmentTypeSelectorBinding.inflate(inflater, container, false)
    }

    override fun invalidate() = withState(viewModel, timelineViewModel) { viewState, timelineState ->
        super.invalidate()
        views.location.visibility = if (viewState.isLocationVisible) View.VISIBLE else View.GONE
        views.voiceBroadcast.visibility = if (viewState.isVoiceBroadcastVisible) View.VISIBLE else View.GONE
        views.poll.visibility = if (!timelineState.isThreadTimeline()) View.VISIBLE else View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.gallery.debouncedClicks { onAttachmentSelected(AttachmentType.GALLERY) }
        views.stickers.debouncedClicks { onAttachmentSelected(AttachmentType.STICKER) }
        views.file.debouncedClicks { onAttachmentSelected(AttachmentType.FILE) }
        views.voiceBroadcast.debouncedClicks { onAttachmentSelected(AttachmentType.VOICE_BROADCAST) }
        views.poll.debouncedClicks { onAttachmentSelected(AttachmentType.POLL) }
        views.location.debouncedClicks { onAttachmentSelected(AttachmentType.LOCATION) }
        views.camera.debouncedClicks { onAttachmentSelected(AttachmentType.CAMERA) }
        views.contact.debouncedClicks { onAttachmentSelected(AttachmentType.CONTACT) }
    }

    private fun onAttachmentSelected(attachmentType: AttachmentType) {
        val action = AttachmentTypeSelectorSharedAction.SelectAttachmentTypeAction(attachmentType)
        sharedActionViewModel.post(action)
        dismiss()
    }

    companion object {
        fun show(fragmentManager: FragmentManager) {
            val bottomSheet = AttachmentTypeSelectorBottomSheet()
            bottomSheet.show(fragmentManager, "AttachmentTypeSelectorBottomSheet")
        }
    }
}
