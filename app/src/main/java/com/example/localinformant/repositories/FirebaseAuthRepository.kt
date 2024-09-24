package com.example.localinformant.repositories

import com.example.localinformant.constants.AppConstants
import com.example.localinformant.models.LoginUserResponse
import com.example.localinformant.models.RegisterCompanyRequest
import com.example.localinformant.models.RegisterPersonRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun loginWithEmail(email: String, password: String): LoginUserResponse {
        try {
            val authResponse = auth.signInWithEmailAndPassword(email, password).await()
            val isEmailVerified = authResponse.user?.isEmailVerified
            if (isEmailVerified == true) {
                return LoginUserResponse(true, "")
            } else {
                authResponse.user?.sendEmailVerification()
                return LoginUserResponse(
                    false,
                    "Email not verified. Check your email for verification"
                )
            }
        } catch (e: Exception) {
            return LoginUserResponse(false, e.message.toString())
        }
    }

    suspend fun resetPassword(email: String): LoginUserResponse {
        try {
            auth.sendPasswordResetEmail(email).await()
            return LoginUserResponse(true, "")
        } catch (e: Exception) {
            return LoginUserResponse(false, e.message.toString())
        }
    }

    suspend fun registerPersonWithEmail(request: RegisterPersonRequest): LoginUserResponse {
        try {
            val authResponse =
                auth.createUserWithEmailAndPassword(request.email, request.password).await()
            val currentUser = auth.currentUser
            val data = hashMapOf(
                AppConstants.ID to currentUser?.uid!!,
                AppConstants.FIRST_NAME to request.firstName,
                AppConstants.LAST_NAME to request.lastName,
                AppConstants.FULL_NAME to "${request.firstName} ${request.lastName}",
                AppConstants.EMAIL to request.email,
                AppConstants.TOKEN to "",
                AppConstants.FOLLOWING to 0
            )
            db.collection(AppConstants.PERSONS).document(currentUser.uid).set(data).await()
            authResponse.user?.sendEmailVerification()
            return LoginUserResponse(true, "")
        } catch (e: Exception) {
            return LoginUserResponse(false, e.message.toString())
        }
    }

    suspend fun registerCompanyWithEmail(request: RegisterCompanyRequest): LoginUserResponse {
        try {
            val authResponse =
                auth.createUserWithEmailAndPassword(request.companyEmail, request.password).await()
            val currentUser = auth.currentUser
            val data = hashMapOf(
                AppConstants.ID to currentUser?.uid!!,
                AppConstants.COMPANY_NAME to request.companyName,
                AppConstants.COMPANY_EMAIL to request.companyEmail,
                AppConstants.FIRST_NAME to request.firstName,
                AppConstants.LAST_NAME to request.lastName,
                AppConstants.EMAIL to request.email,
                AppConstants.TOKEN to "",
                AppConstants.FOLLOWERS to 0
            )
            db.collection(AppConstants.COMPANIES).document(currentUser.uid).set(data).await()
            authResponse.user?.sendEmailVerification()
            return LoginUserResponse(true, "")
        } catch (e: Exception) {
            return LoginUserResponse(false, e.message.toString())
        }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): LoginUserResponse {
        try {
            val user = auth.currentUser!!
            val email = user.email
            val credential = email?.let { EmailAuthProvider.getCredential(it, oldPassword) }
            credential?.let { user.reauthenticate(it).await() }
            user.updatePassword(newPassword).await()
            return LoginUserResponse(true, "")
        } catch (e: Exception) {
            return LoginUserResponse(false, e.message.toString())
        }

    }

    suspend fun deleteUser(): LoginUserResponse {
        try {
            auth.currentUser!!.delete().await()
            return LoginUserResponse(true, "")
        } catch (e: Exception) {
            return LoginUserResponse(false, e.message.toString())
        }
    }

    suspend fun deleteUserData(userId: String, userType: String): LoginUserResponse {
        try {
            db.collection(userType).document(userId).delete().await()
            return LoginUserResponse(true, "")
        } catch (e: Exception) {
            return LoginUserResponse(false, e.message.toString())
        }
    }

    fun logout() = auth.signOut()

    fun currentUser(): FirebaseUser? = auth.currentUser
}