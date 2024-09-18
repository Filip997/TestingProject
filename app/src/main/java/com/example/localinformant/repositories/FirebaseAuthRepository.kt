package com.example.localinformant.repositories

import com.example.localinformant.constants.AppConstants
import com.example.localinformant.models.RegisterCompanyRequest
import com.example.localinformant.models.RegisterPersonRequest
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import io.reactivex.Completable

class FirebaseAuthRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun loginWithEmail(email: String, password: String) =
        Completable.create { emitter -> // Firebase login with email and password
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (!emitter.isDisposed) {
                    if (it.isSuccessful) {
                        it.addOnSuccessListener { result ->
                            result.user?.let { user ->
                                if (user.isEmailVerified)
                                    emitter.onComplete()
                                else {
                                    emitter.onError(Throwable("Email not verified. Check your email for verification"))
                                    user.sendEmailVerification()
                                }
                            }
                        }
                    } else
                        emitter.onError(it.exception!!)

                }
            }
        }


    fun resetPassword(email: String) =
        Completable.create { emitter ->
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if (!emitter.isDisposed) {
                        if (it.isSuccessful)
                            emitter.onComplete()
                        else
                            emitter.onError(it.exception!!)
                    }
                }
        }

    fun registerPersonWithEmail(request: RegisterPersonRequest) =
        Completable.create { emitter ->
            auth.createUserWithEmailAndPassword(request.email, request.password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        val data = hashMapOf(
                            AppConstants.FIRST_NAME to request.firstName,
                            AppConstants.LAST_NAME to request.lastName,
                            AppConstants.FULL_NAME to "${request.firstName} ${request.lastName}",
                            AppConstants.EMAIL to request.email,
                            AppConstants.PASSWORD to request.password
                        )
                        db.collection(AppConstants.PERSONS).document(currentUser?.uid!!).set(data)
                            .addOnCompleteListener { dataTask ->
                                if (!emitter.isDisposed) {
                                    if (dataTask.isSuccessful) {
                                        emitter.onComplete()
                                        task.addOnSuccessListener { result -> result.user?.sendEmailVerification() }
                                    } else {
                                        emitter.onError(dataTask.exception!!)
                                    }
                                }
                            }

                    } else {
                        emitter.onError(task.exception!!)
                    }

                }
        }

    fun registerCompanyWithEmail(request: RegisterCompanyRequest) =
        Completable.create { emitter -> // Firebase sign up with email and password
            auth.createUserWithEmailAndPassword(request.email, request.password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        val data = hashMapOf(
                            AppConstants.COMPANY_NAME to request.companyName,
                            AppConstants.COMPANY_EMAIL to request.companyEmail,
                            AppConstants.FIRST_NAME to request.firstName,
                            AppConstants.LAST_NAME to request.lastName,
                            AppConstants.EMAIL to request.email,
                            AppConstants.PASSWORD to request.password
                        )
                        db.collection(AppConstants.COMPANIES).document(currentUser?.uid!!).set(data)
                            .addOnCompleteListener { dataTask ->
                                if (!emitter.isDisposed) {
                                    if (dataTask.isSuccessful) {
                                        emitter.onComplete()
                                        task.addOnSuccessListener { result -> result.user?.sendEmailVerification() }
                                    } else {
                                        emitter.onError(dataTask.exception!!)
                                    }
                                }
                            }

                    } else {
                        emitter.onError(task.exception!!)
                    }

                }
        }

    fun changePassword(oldPassword: String, newPassword: String) =
        Completable.create { emitter ->
            val user = auth.currentUser!!
            val email = user.email
            email?.let {
                val credential = EmailAuthProvider.getCredential(email, oldPassword)
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener {
                                    if (!emitter.isDisposed) {
                                        if (it.isSuccessful)
                                            emitter.onComplete()
                                        else
                                            emitter.onError(it.exception!!)
                                    }
                                }
                        } else {
                            if (!emitter.isDisposed)
                                emitter.onError(task.exception!!)
                        }
                    }
            }


        }

    fun deleteUser(): Completable =
        Completable.create { emitter ->
            auth.currentUser!!
                .delete()
                .addOnCompleteListener { task ->
                    if (!emitter.isDisposed) {
                        if (task.isSuccessful)
                            emitter.onComplete()
                        else
                            emitter.onError(task.exception!!)
                    }
                }
        }

    fun deleteUserData(userId: String, userType: String): Completable =
        Completable.create { emitter ->
            db.collection(userType)
                .document(userId)
                .delete()
                .addOnCompleteListener {
                    if (!emitter.isDisposed) {
                        if (it.isSuccessful)
                            emitter.onComplete()
                        else
                            emitter.onError(it.exception!!)
                    }
                }
        }

    fun logout() = auth.signOut()

    fun currentUser(): FirebaseUser? = auth.currentUser
}