package com.example.localinformant.views.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.localinformant.R
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.constants.NavFunctions.isFragmentInBackStack
import com.example.localinformant.constants.NavFunctions.popUpDefaultNavigation
import com.example.localinformant.databinding.ActivityMainBinding
import com.example.localinformant.views.fragments.HomeFragment
import com.example.localinformant.views.fragments.MyAccountFragment
import com.example.localinformant.views.fragments.NotificationsFragment
import com.example.localinformant.views.fragments.SearchFragment
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var userType: String? = null
    private lateinit var navController: NavController
    private val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        setNavigation()
        setBottomNavMenu()


    }

    private fun setNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    private fun setBottomNavMenu() {
        binding.bottomNavMenu.setupWithNavController(navController)

        binding.bottomNavMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    if (navController.isFragmentInBackStack(R.id.homeFragment)) {
                        navController.popBackStack(R.id.homeFragment, false)
                    } else {
                        navController.navigate(
                            R.id.homeFragment, bundleOf(),
                            popUpDefaultNavigation()
                        )
                    }
                    true
                }

                R.id.searchFragment -> {
                    if (navController.isFragmentInBackStack(R.id.searchFragment)) {
                        navController.popBackStack(R.id.searchFragment, false)
                    } else {
                        navController.navigate(
                            R.id.searchFragment, bundleOf(),
                            popUpDefaultNavigation()
                        )
                    }
                    true
                }


                R.id.notificationsFragment -> {
                    if (navController.isFragmentInBackStack(R.id.notificationsFragment)) {
                        navController.popBackStack(R.id.notificationsFragment, false)
                    } else {
                        navController.navigate(
                            R.id.notificationsFragment, bundleOf(),
                            popUpDefaultNavigation()
                        )
                    }
                    true
                }

                R.id.myAccountFragment -> {
                    if (navController.isFragmentInBackStack(R.id.myAccountFragment)) {
                        navController.popBackStack(R.id.myAccountFragment, false)
                    } else {
                        navController.navigate(
                            R.id.myAccountFragment, bundleOf(),
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