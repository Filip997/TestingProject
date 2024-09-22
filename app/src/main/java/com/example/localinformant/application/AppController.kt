package com.example.localinformant.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.localinformant.constants.FirebaseApiConstants
import com.example.localinformant.constants.SharedPrefKeys
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppController : Application() {

    companion object {
        lateinit var userSharedPreferences: SharedPreferences
    }

    /*val firebaseAuth by lazy {
        Firebase.auth
    }

    val firebaseFirestore by lazy {
        Firebase.firestore
    }*/

    val retrofit by lazy {
        Retrofit.Builder().baseUrl(FirebaseApiConstants.BASE_URL).addConverterFactory(
            GsonConverterFactory.create()).build()
    }

    override fun onCreate() {
        super.onCreate()

        userSharedPreferences = getSharedPreferences(SharedPrefKeys.USER_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }
}