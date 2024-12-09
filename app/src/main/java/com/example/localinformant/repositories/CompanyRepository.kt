package com.example.localinformant.repositories

import android.util.Log
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.constants.PreferencesManager
import com.example.localinformant.models.Company
import com.example.localinformant.models.Person
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CompanyRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    /*suspend fun searchCompaniesByName(): List<Company> =
        withContext(Dispatchers.IO) {
            try {
                val querySnapshot = db.collection(AppConstants.COMPANIES)
                    .orderBy("companyName")
                    .get()
                    .await()

                querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Company::class.java)
                }
            } catch (e: Exception) {
                listOf<Company>()
            }
        }*/

    suspend fun searchCompaniesByName(): List<Company> {
        return db.collection(AppConstants.COMPANIES)
            .orderBy("companyName")
            .get()
            .await()
            .toObjects(Company::class.java)
    }

    suspend fun getCurrentCompany(): Company? =
        withContext(Dispatchers.IO) {
            try {
               val documentSnapshot = db.collection(AppConstants.COMPANIES)
                   .document(auth.currentUser?.uid!!)
                   .get()
                   .await()

                documentSnapshot.toObject(Company::class.java)
            } catch (e: Exception) {
                null
            }
        }

    suspend fun getCompanyById(id: String): Company? =
        withContext(Dispatchers.IO) {
            try {
                val documentSnapshot = db.collection(AppConstants.COMPANIES)
                    .document(id)
                    .get()
                    .await()

                documentSnapshot.toObject(Company::class.java)
            } catch (e: Exception) {
                Log.d("companyById", e.message.toString())
                null
            }
        }

    suspend fun updateCompanyToken(token: String) {
        withContext(Dispatchers.IO) {
            try {
                db.collection(AppConstants.COMPANIES).document(auth.currentUser?.uid!!).update("token", token)
            } catch (e: Exception) {
                Log.d("updateCompanyToken", e.printStackTrace().toString())
            }
        }
    }

    suspend fun followUnfollowCompany(companyId: String) {
        withContext(Dispatchers.IO) {
            try {
                val currentPerson = PreferencesManager.getPerson()

                val currentCompany = db.collection(AppConstants.COMPANIES)
                    .document(companyId)
                    .get()
                    .await()
                    .toObject<Company>()

                if (currentPerson != null && currentCompany != null) {
                    val personFollowing = currentPerson.following
                    val companyFollowers = currentCompany.followers

                    var currentPersonFollowsCurrentCompany = false
                    var currentCompanyIsFollowedByCurrentPerson = false

                    for (id in personFollowing) {
                        if (id == companyId) {
                            currentPersonFollowsCurrentCompany = true
                            break
                        }
                    }

                    for (id in companyFollowers) {
                        if (id == currentPerson.id) {
                            currentCompanyIsFollowedByCurrentPerson = true
                            break
                        }
                    }

                    if (!currentPersonFollowsCurrentCompany && !currentCompanyIsFollowedByCurrentPerson) {
                        personFollowing.add(companyId)
                        companyFollowers.add(currentPerson.id)

                        db.collection(AppConstants.PERSONS).document(currentPerson.id).update("following", personFollowing)
                        db.collection(AppConstants.COMPANIES).document(companyId).update("followers", companyFollowers)
                    } else if (currentPersonFollowsCurrentCompany && currentCompanyIsFollowedByCurrentPerson) {
                        personFollowing.remove(companyId)
                        companyFollowers.remove(currentPerson.id)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }
}