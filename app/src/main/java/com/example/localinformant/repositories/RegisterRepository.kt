package com.example.localinformant.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.models.RegisterCompanyRequest
import com.example.localinformant.models.RegisterPersonRequest
import com.example.localinformant.models.RegisterUserResponse
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun registerPersonWithEmailAndPassword(request: RegisterPersonRequest, registerPersonMutable: MutableLiveData<RegisterUserResponse>) {
        withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(request.email, request.password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser

                    val data = hashMapOf(
                        AppConstants.FIRST_NAME to request.firstName,
                        AppConstants.LAST_NAME to request.lastName,
                        AppConstants.EMAIL to request.lastName,
                        AppConstants.PASSWORD to request.password
                    )

                    db.collection(AppConstants.PERSONS).document(currentUser?.uid!!).set(data).addOnCompleteListener { dataTask ->
                        if (dataTask.isSuccessful) {
                            registerPersonMutable.postValue(RegisterUserResponse(
                                true,
                                "Successfully created user"
                            ))
                        } else {
                            registerPersonMutable.postValue(RegisterUserResponse(
                                false,
                                dataTask.exception?.message
                            ))
                        }
                    }
                } else {
                    Log.d("emailPassword", task.exception.toString())
                    registerPersonMutable.postValue(RegisterUserResponse(
                        false,
                        task.exception?.message
                    ))
                }
            }
        }
    }

    suspend fun registerCompanyWithEmailAndPassword(request: RegisterCompanyRequest, registerCompanyMutable: MutableLiveData<RegisterUserResponse>) {
        withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(request.companyEmail, request.password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser

                    val data = hashMapOf(
                        AppConstants.COMPANY_NAME to request.companyName,
                        AppConstants.COMPANY_EMAIL to request.companyEmail,
                        AppConstants.FIRST_NAME to request.firstName,
                        AppConstants.LAST_NAME to request.lastName,
                        AppConstants.EMAIL to request.lastName,
                        AppConstants.PASSWORD to request.password
                    )

                    db.collection(AppConstants.COMPANIES).document(currentUser?.uid!!).set(data).addOnCompleteListener { dataTask ->
                        if (dataTask.isSuccessful) {
                            registerCompanyMutable.postValue(RegisterUserResponse(
                                true,
                                "Successfully created user"
                            ))
                        } else {
                            registerCompanyMutable.postValue(RegisterUserResponse(
                                false,
                                dataTask.exception?.message
                            ))
                        }
                    }
                } else {
                    Log.d("emailPassword", task.exception.toString())
                    registerCompanyMutable.postValue(RegisterUserResponse(
                        false,
                        task.exception?.message
                    ))
                }
            }
        }
    }
}