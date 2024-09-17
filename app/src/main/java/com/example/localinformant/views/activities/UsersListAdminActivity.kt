package com.example.localinformant.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.localinformant.databinding.ActivityUsersListAdminBinding

class UsersListAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsersListAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}