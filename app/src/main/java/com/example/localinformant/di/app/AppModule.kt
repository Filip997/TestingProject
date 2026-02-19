package com.example.localinformant.di.app

import android.content.Context
import android.content.SharedPreferences
import com.example.localinformant.auth.data.repositories.FirebaseAuthRepositoryImpl
import com.example.localinformant.auth.domain.repositories.FirebaseAuthRepository
import com.example.localinformant.constants.SharedPrefKeys
import com.example.localinformant.core.data.repositories.PreferencesRepositoryImpl
import com.example.localinformant.core.data.network.NetworkCheckerImpl
import com.example.localinformant.core.domain.repositories.PreferencesRepository
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.di.qualifiers.AppSharedPreferences
import com.example.localinformant.di.qualifiers.UserSharedPreferences
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    companion object {
        @Provides
        @Singleton
        @UserSharedPreferences
        fun userSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences(SharedPrefKeys.USER_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        }

        @Provides
        @Singleton
        @AppSharedPreferences
        fun appSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences(SharedPrefKeys.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        }

        @Provides
        @Singleton
        fun firebaseAuth(): FirebaseAuth {
            return Firebase.auth
        }

        @Provides
        @Singleton
        fun firebaseFirestore(): FirebaseFirestore {
            return Firebase.firestore
        }
    }

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepositoryImpl
    ): FirebaseAuthRepository

    @Binds
    @Singleton
    abstract fun bindNetworkChecker(
        impl: NetworkCheckerImpl
    ): NetworkChecker
}