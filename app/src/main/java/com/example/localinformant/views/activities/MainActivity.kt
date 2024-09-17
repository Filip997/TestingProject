package com.example.localinformant.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.localinformant.R
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.IntentKeys
import com.example.localinformant.databinding.ActivityMainBinding
import com.example.localinformant.views.fragments.HomeFragment
import com.example.localinformant.views.fragments.MyAccountFragment
import com.example.localinformant.views.fragments.NotificationsFragment
import com.example.localinformant.views.fragments.SearchFragment
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var userType: String? = null

    private lateinit var selectorFragment: Fragment
    private var currentFragment: Fragment = HomeFragment()
    private val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(IntentKeys.USER_TYPE)) {
            userType = intent.getStringExtra(IntentKeys.USER_TYPE)
        }

        bundle.putString(IntentKeys.USER_TYPE, userType)
        currentFragment.arguments = bundle

        setupBottomNavigationMenu()


    }

    private fun setupBottomNavigationMenu() {
        if (userType == AppConstants.PERSON) {
            val menu = binding.bottomNavigationView.menu
            menuInflater.inflate(R.menu.bottom_navigation_menu_person, menu)



            binding.bottomNavigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {

                    when (item.itemId) {
                        R.id.home_menu_person -> {
                            selectorFragment = HomeFragment()
                            selectorFragment.arguments = bundle
                        }
                        R.id.search_menu_person -> {
                            selectorFragment = SearchFragment()
                            selectorFragment.arguments = bundle
                        }
                        R.id.notifications_menu_person -> {
                            selectorFragment = NotificationsFragment()
                            selectorFragment.arguments = bundle
                        }
                        R.id.my_account_menu_person -> {
                            selectorFragment = MyAccountFragment()
                            selectorFragment.arguments = bundle
                        }
                    }

                    if (selectorFragment != null) {
                        if (selectorFragment.javaClass.name != currentFragment.javaClass.name) {
                            Log.i("fragmentSelector", selectorFragment.javaClass.name)
                            Log.i("fragmentCurrent", currentFragment.javaClass.name)
                            currentFragment = selectorFragment
                            val fragmentTransaction = supportFragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.fragment_container_view, selectorFragment)
                            fragmentTransaction.commit()
                        }
                    }

                    return true
                }

            })
        } else if (userType == AppConstants.COMPANY) {
            val menu = binding.bottomNavigationView.menu
            menuInflater.inflate(R.menu.bottom_navigation_menu_company, menu)

            binding.bottomNavigationView.setOnItemSelectedListener(object : NavigationBarView.OnItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {

                    when (item.itemId) {
                        R.id.home_menu_company -> {
                            selectorFragment = HomeFragment()
                            selectorFragment.arguments = bundle
                        }
                        R.id.search_menu_company -> {
                            selectorFragment = SearchFragment()
                            selectorFragment.arguments = bundle
                        }
                        R.id.create_post_menu_company -> {

                        }
                        R.id.notifications_menu_company -> {
                            selectorFragment = NotificationsFragment()
                            selectorFragment.arguments = bundle
                        }
                        R.id.my_account_menu_company -> {
                            selectorFragment = MyAccountFragment()
                            selectorFragment.arguments = bundle
                        }
                    }

                    if (selectorFragment != null) {
                        if (selectorFragment.javaClass.name != currentFragment.javaClass.name) {
                            Log.i("fragmentSelector", selectorFragment.javaClass.name)
                            Log.i("fragmentCurrent", currentFragment.javaClass.name)
                            currentFragment = selectorFragment
                            val fragmentTransaction = supportFragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.fragment_container_view, selectorFragment)
                            fragmentTransaction.commit()
                        }
                    }

                    return true
                }

            })
        }
    }
}