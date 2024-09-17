package com.example.localinformant.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.localinformant.models.LoginUserResponse
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth

class LoginRepository {

    private val auth = Firebase.auth

    fun loginUserWithEmailAndPassword(email: String, password: String, loginUserMutable: MutableLiveData<LoginUserResponse>) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                loginUserMutable.postValue(
                    LoginUserResponse(
                        true,
                        "Successfully logged in"
                    )
                )
            } else {
                Log.d("emailPassword", "auth error")

                try {
                    throw authTask.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    loginUserMutable.postValue(
                        LoginUserResponse(
                            false,
                            "User with this email doesn't exist"
                        )
                    )
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    loginUserMutable.postValue(
                        LoginUserResponse(
                            false,
                            "Invalid credentials."
                        )
                    )
                } catch (e: Exception) {
                    loginUserMutable.postValue(
                        LoginUserResponse(
                            false,
                            authTask.exception?.message
                        )
                    )
                }
            }
        }
    }
}