package com.example.localinformant.repositories

import com.example.localinformant.constants.AppConstants
import com.example.localinformant.models.Company
import com.example.localinformant.models.LoginRegisterCompanyResponse
import com.example.localinformant.models.LoginRegisterPersonResponse
import com.example.localinformant.models.LoginUserResponse
import com.example.localinformant.models.Person
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

    suspend fun loginPersonWithEmail(email: String, password: String): LoginRegisterPersonResponse {
        try {
            val authResponse = auth.signInWithEmailAndPassword(email, password).await()
            val isEmailVerified = authResponse.user?.isEmailVerified
            if (isEmailVerified == true) {
                val loggedPerson = getPersonById(authResponse?.user?.uid!!)
                return LoginRegisterPersonResponse(true, "", loggedPerson)
            } else {
                authResponse.user?.sendEmailVerification()
                return LoginRegisterPersonResponse(
                    false,
                    "Email not verified. Check your email for verification",
                    null
                )
            }
        } catch (e: Exception) {
            return LoginRegisterPersonResponse(false, e.message.toString(), null)
        }
    }

    suspend fun loginCompanyWithEmail(email: String, password: String): LoginRegisterCompanyResponse {
        try {
            val authResponse = auth.signInWithEmailAndPassword(email, password).await()
            val isEmailVerified = authResponse.user?.isEmailVerified
            if (isEmailVerified == true) {
                val loggedCompany = getCompanyById(authResponse?.user?.uid!!)
                return LoginRegisterCompanyResponse(true, "", loggedCompany)
            } else {
                authResponse.user?.sendEmailVerification()
                return LoginRegisterCompanyResponse(
                    false,
                    "Email not verified. Check your email for verification",
                    null
                )
            }
        } catch (e: Exception) {
            return LoginRegisterCompanyResponse(false, e.message.toString(), null)
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

    suspend fun registerPersonWithEmail(request: RegisterPersonRequest): LoginRegisterPersonResponse {
        try {
            val authResponse =
                auth.createUserWithEmailAndPassword(request.email, request.password).await()
            val currentUser = auth.currentUser

            val person = Person(
                currentUser?.uid!!,
                request.firstName,
                request.lastName,
                "${request.firstName} ${request.lastName}",
                request.email,
                "",
                listOf()
            )

            val data = hashMapOf(
                AppConstants.ID to person.id,
                AppConstants.FIRST_NAME to person.firstName,
                AppConstants.LAST_NAME to person.lastName,
                AppConstants.FULL_NAME to person.fullName,
                AppConstants.EMAIL to person.email,
                AppConstants.TOKEN to person.token,
                AppConstants.FOLLOWING to person.following
            )
            db.collection(AppConstants.PERSONS).document(currentUser.uid).set(data).await()
            authResponse.user?.sendEmailVerification()
            return LoginRegisterPersonResponse(true, "", person)
        } catch (e: Exception) {
            return LoginRegisterPersonResponse(false, e.message.toString(), null)
        }
    }

    suspend fun registerCompanyWithEmail(request: RegisterCompanyRequest): LoginRegisterCompanyResponse {
        try {
            val authResponse =
                auth.createUserWithEmailAndPassword(request.companyEmail, request.password).await()
            val currentUser = auth.currentUser

            val company = Company(
                currentUser?.uid!!,
                request.companyName,
                request.companyEmail,
                request.email,
                request.firstName,
                request.lastName,
                "",
                listOf()
            )

            val data = hashMapOf(
                AppConstants.ID to company.id,
                AppConstants.COMPANY_NAME to company.companyName,
                AppConstants.COMPANY_EMAIL to company.companyEmail,
                AppConstants.FIRST_NAME to company.firstName,
                AppConstants.LAST_NAME to company.lastName,
                AppConstants.EMAIL to company.email,
                AppConstants.TOKEN to company.token,
                AppConstants.FOLLOWERS to company.followers
            )
            db.collection(AppConstants.COMPANIES).document(currentUser.uid).set(data).await()
            authResponse.user?.sendEmailVerification()
            return LoginRegisterCompanyResponse(true, "", company)
        } catch (e: Exception) {
            return LoginRegisterCompanyResponse(false, e.message.toString(), null)
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

    private suspend fun getPersonById(id: String): Person? {
        return db.collection(AppConstants.PERSONS)
            .document(id)
            .get()
            .await()
            .toObject(Person::class.java)
    }

    private suspend fun getCompanyById(id: String): Company? {
        return db.collection(AppConstants.COMPANIES)
            .document(id)
            .get()
            .await()
            .toObject(Company::class.java)
    }

    fun logout() = auth.signOut()

    fun currentUser(): FirebaseUser? = auth.currentUser
}