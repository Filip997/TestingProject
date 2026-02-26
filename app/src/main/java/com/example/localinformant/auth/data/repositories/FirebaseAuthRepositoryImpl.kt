package com.example.localinformant.auth.data.repositories

import com.example.localinformant.auth.domain.repositories.FirebaseAuthRepository
import com.example.localinformant.auth.domain.usecases.RegisterUserData
import com.example.localinformant.auth.domain.error.AuthError
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.core.domain.error.Error
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.models.Company
import com.example.localinformant.models.LoginUserResponse
import com.example.localinformant.models.Person
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val cloudMessaging: FirebaseMessaging,
    private val networkChecker: NetworkChecker
) : FirebaseAuthRepository {

    override suspend fun registerPerson(request: RegisterUserData.Person): Result<User, Error> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(AuthError.NO_INTERNET_CONNECTION)
            }

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
                mutableListOf()
            )

            db.collection(AppConstants.PERSONS).document(currentUser.uid).set(person).await()
            authResponse.user?.sendEmailVerification()

            Result.Success(person)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.Error(AuthError.USER_ALREADY_EXISTS)
        } catch (e: FirebaseNetworkException) {
            Result.Error(AuthError.NETWORK_ERROR)
        } catch (e: FirebaseTooManyRequestsException) {
            Result.Error(AuthError.TOO_MANY_REQUESTS)
        } catch (e: Exception) {
            Result.Error(AuthError.UNKNOWN)
        }
    }

    override suspend fun registerCompany(request: RegisterUserData.Company): Result<User, Error> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(AuthError.NO_INTERNET_CONNECTION)
            }

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
                mutableListOf()
            )

            db.collection(AppConstants.COMPANIES).document(currentUser.uid).set(company).await()
            authResponse.user?.sendEmailVerification()

            Result.Success(company)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.Error(AuthError.USER_ALREADY_EXISTS)
        } catch (e: FirebaseNetworkException) {
            Result.Error(AuthError.NETWORK_ERROR)
        } catch (e: FirebaseTooManyRequestsException) {
            Result.Error(AuthError.TOO_MANY_REQUESTS)
        } catch (e: Exception) {
            Result.Error(AuthError.UNKNOWN)
        }
    }

    override suspend fun loginPerson(email: String, password: String): Result<User, Error> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(AuthError.NO_INTERNET_CONNECTION)
            }

            val authResponse = auth.signInWithEmailAndPassword(email, password).await()
            val isEmailVerified = authResponse.user?.isEmailVerified

            if (isEmailVerified == true) {
                val personId = authResponse?.user?.uid!!
                val loggedPerson = getPersonById(personId)

                if (loggedPerson != null) {
                    val token = cloudMessaging.token.await()
                    updatePersonToken(personId, token)

                    Result.Success(loggedPerson)
                } else {
                    Result.Error(AuthError.USER_NOT_FOUND)
                }
            } else {
                authResponse.user?.sendEmailVerification()
                Result.Error(AuthError.EMAIL_NOT_VERIFIED)
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error(AuthError.INVALID_CREDENTIALS)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error(AuthError.USER_NOT_FOUND)
        } catch (e: FirebaseNetworkException) {
            Result.Error(AuthError.NETWORK_ERROR)
        } catch (e: FirebaseTooManyRequestsException) {
            Result.Error(AuthError.TOO_MANY_REQUESTS)
        } catch (e: Exception) {
            Result.Error(AuthError.UNKNOWN)
        }
    }

    private suspend fun getPersonById(id: String): Person? {
        return db.collection(AppConstants.PERSONS)
            .document(id)
            .get()
            .await()
            .toObject(Person::class.java)
    }

    private suspend fun updatePersonToken(id: String, token: String) {
        db.collection(AppConstants.PERSONS)
            .document(id)
            .update("token", token)
            .await()
    }

    override suspend fun loginCompany(email: String, password: String): Result<User, Error> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(AuthError.NO_INTERNET_CONNECTION)
            }

            val authResponse = auth.signInWithEmailAndPassword(email, password).await()
            val isEmailVerified = authResponse.user?.isEmailVerified

            if (isEmailVerified == true) {
                val companyId = authResponse?.user?.uid!!
                val loggedCompany = getCompanyById(companyId)

                if (loggedCompany != null) {
                    val token = cloudMessaging.token.await()
                    updateCompanyToken(companyId, token)

                    Result.Success(loggedCompany)
                } else {
                    Result.Error(AuthError.USER_NOT_FOUND)
                }
            } else {
                authResponse.user?.sendEmailVerification()
                Result.Error(AuthError.EMAIL_NOT_VERIFIED)
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error(AuthError.INVALID_CREDENTIALS)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error(AuthError.USER_NOT_FOUND)
        } catch (e: FirebaseNetworkException) {
            Result.Error(AuthError.NETWORK_ERROR)
        } catch (e: FirebaseTooManyRequestsException) {
            Result.Error(AuthError.TOO_MANY_REQUESTS)
        } catch (e: Exception) {
            Result.Error(AuthError.UNKNOWN)
        }
    }

    private suspend fun getCompanyById(id: String): Company? {
        return db.collection(AppConstants.COMPANIES)
            .document(id)
            .get()
            .await()
            .toObject(Company::class.java)
    }

    private suspend fun updateCompanyToken(id: String, token: String) {
        db.collection(AppConstants.COMPANIES)
            .document(id)
            .update("token", token)
            .await()
    }

    override suspend fun resetPassword(email: String): Result<Unit, Error> {
        return try {
            auth.sendPasswordResetEmail(email).await()

            Result.Success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error(AuthError.USER_NOT_FOUND)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error(AuthError.INVALID_CREDENTIALS)
        } catch (e: FirebaseNetworkException) {
            Result.Error(AuthError.NETWORK_ERROR)
        } catch (e: FirebaseTooManyRequestsException) {
            Result.Error(AuthError.TOO_MANY_REQUESTS)
        } catch (e: Exception) {
            Result.Error(AuthError.UNKNOWN)
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