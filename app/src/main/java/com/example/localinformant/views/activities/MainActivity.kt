package com.example.localinformant.views.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.localinformant.R
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.FirebaseApiConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.constants.NavFunctions.isFragmentInBackStack
import com.example.localinformant.constants.NavFunctions.popUpDefaultNavigation
import com.example.localinformant.constants.PreferencesManager
import com.example.localinformant.databinding.ActivityMainBinding
import com.example.localinformant.databinding.CreatePostPopUpDesignBinding
import com.example.localinformant.models.PostRequest
import com.example.localinformant.viewmodels.CompanyViewModel
import com.example.localinformant.viewmodels.PersonViewModel
import com.example.localinformant.viewmodels.PostViewModel
import com.example.localinformant.viewmodels.UserViewModel
import com.example.localinformant.views.fragments.HomeFragment
import com.example.localinformant.views.fragments.MyAccountFragment
import com.example.localinformant.views.fragments.NotificationsFragment
import com.example.localinformant.views.fragments.SearchFragment
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var personViewModel: PersonViewModel
    private lateinit var companyViewModel: CompanyViewModel
    private lateinit var postViewModel: PostViewModel
    private lateinit var userViewModel: UserViewModel
    private var userType: String? = null
    private lateinit var navController: NavController
    private val bundle = Bundle()
    private var currentFragment: Fragment = HomeFragment()

    private val newTokenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val newToken = intent?.getStringExtra(IntentKeys.USER_TOKEN)

            userViewModel.setNewToken(newToken!!)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LocalBroadcastManager.getInstance(this).registerReceiver(newTokenReceiver, IntentFilter(AppConstants.NEW_TOKEN))

        personViewModel = ViewModelProvider(this)[PersonViewModel::class.java]
        companyViewModel = ViewModelProvider(this)[CompanyViewModel::class.java]
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        if (intent.hasExtra(IntentKeys.USER_TYPE)) {
            userType = intent.getStringExtra(IntentKeys.USER_TYPE)
        } else {
            val sharedPreferences = getSharedPreferences(
                AppConstants.SHARED_PREFS,
                Context.MODE_PRIVATE
            )
            userType =  sharedPreferences.getString(IntentKeys.USER_TYPE,"")
        }

        bundle.putString(IntentKeys.USER_TYPE, userType)

        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseApiConstants.TOPIC)

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                if (userType == AppConstants.PERSON) {
                    personViewModel.updatePersonToken(it.result)
                } else if (userType == AppConstants.COMPANY) {
                    companyViewModel.updateCompanyToken(it.result)
                }
            }
        }

        setupViewModels()
        setNavigation()
        setBottomNavMenu()
        setCreatePostClickListener()
    }

    private fun setupViewModels() {
        lifecycleScope.launch {
            userViewModel.newTokenFlow.collect { token ->
                if (userType == AppConstants.PERSON) {
                    personViewModel.updatePersonToken(token)
                } else if (userType == AppConstants.COMPANY) {
                    companyViewModel.updateCompanyToken(token)
                }
            }
        }

        postViewModel.createPostLiveData.observe(this) { response ->
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
        }

        postViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

    }

    private fun setBottomNavMenu() {
        binding.bottomNavView.setupWithNavController(navController)

        if (userType == AppConstants.PERSON) {
            binding.bottomNavView.menu.getItem(0).title = "Home"
            binding.bottomNavView.menu.removeItem(R.id.createPost)
            binding.fabCreatePost.visibility = View.GONE
        } else {
            binding.bottomNavView.menu.getItem(0).title = "My posts"
            binding.fabCreatePost.visibility = View.VISIBLE
        }

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    currentFragment = HomeFragment()

                    if (navController.isFragmentInBackStack(R.id.homeFragment)) {
                        navController.popBackStack(R.id.homeFragment, false)
                    } else {
                        navController.navigate(
                            R.id.homeFragment, bundle,
                            popUpDefaultNavigation()
                        )
                    }
                    true
                }

                R.id.searchFragment -> {
                    currentFragment = SearchFragment()

                    if (navController.isFragmentInBackStack(R.id.searchFragment)) {
                        navController.popBackStack(R.id.searchFragment, false)
                    } else {
                        navController.navigate(
                            R.id.searchFragment, bundle,
                            popUpDefaultNavigation()
                        )
                    }
                    true
                }


                R.id.notificationsFragment -> {
                    currentFragment = NotificationsFragment()

                    if (navController.isFragmentInBackStack(R.id.notificationsFragment)) {
                        navController.popBackStack(R.id.notificationsFragment, false)
                    } else {
                        navController.navigate(
                            R.id.notificationsFragment, bundle,
                            popUpDefaultNavigation()
                        )
                    }
                    true
                }

                R.id.myAccountFragment -> {
                    currentFragment = MyAccountFragment()

                    if (navController.isFragmentInBackStack(R.id.myAccountFragment)) {
                        navController.popBackStack(R.id.myAccountFragment, false)
                    } else {
                        navController.navigate(
                            R.id.myAccountFragment, bundle,
                            popUpDefaultNavigation()
                        )
                    }
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun setCreatePostClickListener() {
        if (userType == AppConstants.COMPANY) {
            binding.fabCreatePost.setOnClickListener {
                //val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)

                Log.d("currentFragment", currentFragment.toString())

                if (currentFragment is HomeFragment) {
                    Log.d("currentFragmentIf", currentFragment.toString())
                    createPopUpWindow()
                }
            }
        }
    }

    private fun createPopUpWindow() {
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.create_post_pop_up_design, null)

        val screenWidth = resources.displayMetrics.widthPixels

        val width = screenWidth - 2 * 30
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true

        val createPostPopUpWindow = PopupWindow(view, width, height, focusable)
        createPostPopUpWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val btnPost: Button = view.findViewById(R.id.btn_post)
        val btnCancel: Button = view.findViewById(R.id.btn_cancel)
        val etPost: TextInputEditText = view.findViewById(R.id.et_post)

        btnPost.setOnClickListener {
            val uuid = UUID.randomUUID().toString()
            val postText = etPost.text.toString()

            postViewModel.createPost(
                PostRequest(
                    uuid,
                    postText
                )
            )

            createPostPopUpWindow.dismiss()
        }

        btnCancel.setOnClickListener {
            createPostPopUpWindow.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newTokenReceiver)
    }

//    private fun setupBottomNavigationMenu() {
//        if (userType == AppConstants.PERSON) {
//            val menu = binding.bottomNavigationView.menu
//            menuInflater.inflate(R.menu.bottom_navigation_menu_person, menu)
//
//
//
//            binding.bottomNavigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
//                override fun onNavigationItemSelected(item: MenuItem): Boolean {
//
//                    when (item.itemId) {
//                        R.id.home_menu_person -> {
//                            selectorFragment = HomeFragment()
//                            selectorFragment.arguments = bundle
//                        }
//                        R.id.search_menu_person -> {
//                            selectorFragment = SearchFragment()
//                            selectorFragment.arguments = bundle
//                        }
//                        R.id.notifications_menu_person -> {
//                            selectorFragment = NotificationsFragment()
//                            selectorFragment.arguments = bundle
//                        }
//                        R.id.my_account_menu_person -> {
//                            selectorFragment = MyAccountFragment()
//                            selectorFragment.arguments = bundle
//                        }
//                    }
//
//                    if (selectorFragment != null) {
//                        if (selectorFragment.javaClass.name != currentFragment.javaClass.name) {
//                            Log.i("fragmentSelector", selectorFragment.javaClass.name)
//                            Log.i("fragmentCurrent", currentFragment.javaClass.name)
//                            currentFragment = selectorFragment
//                            val fragmentTransaction = supportFragmentManager.beginTransaction()
//                            fragmentTransaction.replace(R.id.fragment_container_view, selectorFragment)
//                            fragmentTransaction.commit()
//                        }
//                    }
//
//                    return true
//                }
//
//            })
//        } else if (userType == AppConstants.COMPANY) {
//            val menu = binding.bottomNavigationView.menu
//            menuInflater.inflate(R.menu.bottom_navigation_menu_company, menu)
//
//            binding.bottomNavigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
//                override fun onNavigationItemSelected(item: MenuItem): Boolean {
//
//                    when (item.itemId) {
//                        R.id.home_menu_company -> {
//                            selectorFragment = HomeFragment()
//                            selectorFragment.arguments = bundle
//                        }
//                        R.id.search_menu_company -> {
//                            selectorFragment = SearchFragment()
//                            selectorFragment.arguments = bundle
//                        }
//                        R.id.create_post_menu_company -> {
//
//                        }
//                        R.id.notifications_menu_company -> {
//                            selectorFragment = NotificationsFragment()
//                            selectorFragment.arguments = bundle
//                        }
//                        R.id.my_account_menu_company -> {
//                            selectorFragment = MyAccountFragment()
//                            selectorFragment.arguments = bundle
//                        }
//                    }
//
//                    if (selectorFragment != null) {
//                        if (selectorFragment.javaClass.name != currentFragment.javaClass.name) {
//                            Log.i("fragmentSelector", selectorFragment.javaClass.name)
//                            Log.i("fragmentCurrent", currentFragment.javaClass.name)
//                            currentFragment = selectorFragment
//                            val fragmentTransaction = supportFragmentManager.beginTransaction()
//                            fragmentTransaction.replace(R.id.fragment_container_view, selectorFragment)
//                            fragmentTransaction.commit()
//                        }
//                    }
//
//                    return true
//                }
//
//            })
//        }
//    }
}