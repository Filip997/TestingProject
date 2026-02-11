package com.example.localinformant.di.app

import android.content.Context
import android.content.SharedPreferences
import com.example.localinformant.constants.SharedPrefKeys
import com.example.localinformant.core.data.PreferencesRepositoryImpl
import com.example.localinformant.core.domain.PreferencesRepository
import com.example.localinformant.di.qualifiers.AppSharedPreferences
import com.example.localinformant.di.qualifiers.UserSharedPreferences
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
    }

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository
}