package com.example.localinformant.account.presentation.util

import android.app.Activity
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localinformant.R
import com.example.localinformant.account.presentation.adapters.FollowersFollowingAdapter
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.models.FollowerFollowingUserUi
import com.example.localinformant.databinding.FollowersFollowingPopUpDesignBinding
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class FollowersFollowingPopUpWindow @Inject constructor(
    private val activity: Activity
) {
    private lateinit var binding: FollowersFollowingPopUpDesignBinding

    private var popUpWindow: PopupWindow? = null

    fun createPopUpWindow(
        type: String,
        users: List<FollowerFollowingUserUi>,
        goToUserProfile: (String, UserType) -> Unit
    ) {
        val inflater: LayoutInflater = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FollowersFollowingPopUpDesignBinding.inflate(inflater)

        val screenWidth = activity.resources.displayMetrics.widthPixels

        val width = screenWidth - 2 * 200
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true

        if (popUpWindow?.isShowing == true) {
            popUpWindow?.dismiss()
        }

        popUpWindow = PopupWindow(binding.root, width, height, focusable)
        popUpWindow?.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        binding.tvAccountFollowersFollowingTitle.text = when(type) {
            "followers" -> activity.getString(R.string.followers)
            "following" -> activity.getString(R.string.following)
            else -> ""
        }

        val usersAdapter = FollowersFollowingAdapter(
            context = activity,
            users = users,
            goToUserProfile = { userId, userType ->
                popUpWindow?.dismiss()
                goToUserProfile.invoke(userId, userType)
            }
        )
        binding.rvAccountUserFollowersFollowing.layoutManager = LinearLayoutManager(activity)
        binding.rvAccountUserFollowersFollowing.adapter = usersAdapter
    }
}