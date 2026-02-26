package com.example.localinformant.main.presentation.util

import android.app.Activity
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localinformant.databinding.CreatePostPopUpDesignBinding
import com.example.localinformant.main.presentation.adapters.PickedImagesAdapter
import dagger.hilt.android.scopes.ActivityScoped
import java.util.UUID
import javax.inject.Inject

@ActivityScoped
class PopUpWindowManager @Inject constructor(
    private val activity: Activity
) {
    private var binding: CreatePostPopUpDesignBinding? = null
    private var createPostPopUpWindow: PopupWindow? = null

    private var imagePickerListener: ImagePickerListener? = null
    private var sharePostListener: SharePostListener? = null

    private interface ImagePickerListener {
        fun updatePickedImages(uris: List<Uri>)
    }

    interface SharePostListener {
        fun sharePost(id: String, postText: String, uris: List<Uri>)
    }

    private val pickImageLauncher =
        (activity as AppCompatActivity).registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (uris.isNotEmpty()) {
                imagePickerListener?.updatePickedImages(uris)
            }
        }

    fun createPopUpWindow() {
        val inflater: LayoutInflater = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = CreatePostPopUpDesignBinding.inflate(inflater)

        val screenWidth = activity.resources.displayMetrics.widthPixels

        val width = screenWidth - 2 * 30
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true

        createPostPopUpWindow = PopupWindow(binding?.root, width, height, focusable)
        createPostPopUpWindow?.showAtLocation(binding?.root, Gravity.CENTER, 0, 0)

        val pickedImagesAdapter = PickedImagesAdapter(mutableListOf())
        binding?.rvCreatePostPickedImages?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvCreatePostPickedImages?.adapter = pickedImagesAdapter

        binding?.ivCreatePostAttachPic?.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        imagePickerListener = object : ImagePickerListener {
            override fun updatePickedImages(uris: List<Uri>) {
                pickedImagesAdapter.updateList(uris)
                pickedImagesAdapter.notifyDataSetChanged()
            }
        }

        binding?.tvShareTextBtn?.setOnClickListener {
            val uuid = UUID.randomUUID().toString()
            val postText = binding?.etCreatePost?.text.toString()
            val uris = pickedImagesAdapter.getUris()

            sharePostListener?.sharePost(uuid, postText, uris)
        }

        binding?.tvCloseTextBtn?.setOnClickListener {
            createPostPopUpWindow?.dismiss()
        }
    }

    fun showLoader(isLoading: Boolean) {
        if (isLoading) {
            binding?.progressbarCreatePost?.visibility = View.VISIBLE
        } else {
            binding?.progressbarCreatePost?.visibility = View.GONE
        }
    }

    fun closePopUp() {
        createPostPopUpWindow?.dismiss()
        resetProperties()
    }

    private fun resetProperties() {
        binding = null
        createPostPopUpWindow = null
        imagePickerListener = null
        sharePostListener = null
    }

    fun registerListener(listener: SharePostListener) {
        sharePostListener = listener
    }
}