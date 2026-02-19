package com.example.localinformant.core.presentation.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getLayoutBinding(): ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = getLayoutBinding()
        setContentView(binding.root)
        setupWindowInsets(binding.root)
    }

    private fun setupWindowInsets(view: View) {
        enableEdgeToEdge()

        val viewPaddingLeft = view.paddingLeft
        val viewPaddingTop = view.paddingTop
        val viewPaddingRight = view.paddingRight
        val viewPaddingBottom = view.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                viewPaddingLeft + systemBars.left,
                viewPaddingTop + systemBars.top,
                viewPaddingRight + systemBars.right,
                viewPaddingBottom + systemBars.bottom
            )
            insets
        }

        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = true
    }
}