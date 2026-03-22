package com.example.localinformant.home.presentation.util

import android.app.Activity
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.models.ReactionUi
import com.example.localinformant.databinding.ReactionsPopUpDesignBinding
import com.example.localinformant.home.presentation.adapters.PostReactionsAdapter
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ReactionsPopUpWindow @Inject constructor(
    private val activity: Activity
) {
    private lateinit var binding: ReactionsPopUpDesignBinding

    fun createPopUpWindow(
        reactions: List<ReactionUi>,
        goToUserProfile: (String, UserType) -> Unit
    ) {
        val inflater: LayoutInflater = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ReactionsPopUpDesignBinding.inflate(inflater)

        val screenWidth = activity.resources.displayMetrics.widthPixels

        val width = screenWidth - 2 * 200
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true

        val createPostPopUpWindow = PopupWindow(binding.root, width, height, focusable)
        createPostPopUpWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        val postReactionsAdapter = PostReactionsAdapter(
            activity,
            reactions,
            goToUserProfile
        )
        binding.rvHomePostUserReactions.layoutManager = LinearLayoutManager(activity)
        binding.rvHomePostUserReactions.adapter = postReactionsAdapter
    }
}