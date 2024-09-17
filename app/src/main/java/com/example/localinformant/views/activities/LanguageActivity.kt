package com.example.localinformant.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.localinformant.databinding.ActivityLanguageBinding

class LanguageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            val intent = Intent(this@LanguageActivity, LoginChooserActivity::class.java)
            startActivity(intent)
        }
    }
}