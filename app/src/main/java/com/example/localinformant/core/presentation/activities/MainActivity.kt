package com.example.localinformant.core.presentation.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewbinding.ViewBinding
import com.example.localinformant.R
import com.example.localinformant.core.domain.models.UserType
import com.example.localinformant.core.presentation.dialogs.CustomInfoDialog
import com.example.localinformant.core.presentation.navigator.ScreensNavigator
import com.example.localinformant.core.presentation.util.toString
import com.example.localinformant.databinding.ActivityMainBinding
import com.example.localinformant.main.presentation.events.CreatePostEvent
import com.example.localinformant.main.presentation.util.PopUpWindowManager
import com.example.localinformant.main.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    @Inject lateinit var screensNavigator: ScreensNavigator
    @Inject lateinit var popUpWindowManager: PopUpWindowManager

    private val navController: NavController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.findNavController()
    }

    override fun getLayoutBinding(): ViewBinding {
        binding = ActivityMainBinding.inflate(layoutInflater)
        return binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        screensNavigator.onAttachNavController(navController)

        setupViewModels()
        setupClickListeners()
        setBottomNavMenu()
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.mainUiState.collect { state ->

                    popUpWindowManager.showLoader(state.isLoading)

                    when(state.userType) {
                        UserType.PERSON -> {
                            binding.fabMainCreatePost.visibility = View.GONE
                            binding.bottomNavViewMain.menu.removeItem(R.id.createPost)
                        }
                        UserType.COMPANY -> {
                            binding.fabMainCreatePost.visibility = View.VISIBLE
                        }
                        else -> {

                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.createPostEvent.collect { event ->
                    when(event) {
                        is CreatePostEvent.ClosePopUpWindow -> {
                            popUpWindowManager.closePopUp()
                            CustomInfoDialog(
                                title = getString(R.string.successful),
                                message = getString(R.string.post_is_shared_successfully)
                            ).show(supportFragmentManager, "info_dialog")
                        }
                        is CreatePostEvent.ShowError -> {
                            CustomInfoDialog(
                                title = getString(R.string.error),
                                message = event.error.toString(this@MainActivity)
                            ).show(supportFragmentManager, "info_dialog")
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabMainCreatePost.setOnClickListener {
            popUpWindowManager.createPopUpWindow()
        }

        popUpWindowManager.registerListener(object : PopUpWindowManager.SharePostListener {
            override fun sharePost(
                id: String,
                postText: String,
                uris: List<Uri>
            ) {
                mainViewModel.createAPost(id, postText, uris)
            }

        })
    }

    private fun setBottomNavMenu() {
        binding.bottomNavViewMain.setupWithNavController(navController)

        binding.bottomNavViewMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    screensNavigator.navigateToHomeFragment()
                    true
                }

                R.id.searchFragment -> {
                    screensNavigator.navigateToSearchFragment()
                    true
                }

                R.id.notificationsFragment -> {
                    screensNavigator.navigateToNotificationsFragment()
                    true
                }

                R.id.myAccountFragment -> {
                    screensNavigator.navigateToMyAccountFragment()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    companion object {
        fun start(context: Context, withFlags: Boolean, shouldFinish: Boolean) {
            val intent = Intent(context, MainActivity::class.java)

            if (withFlags) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)
            if (shouldFinish) {
                (context as Activity).finish()
            }
        }
    }
}