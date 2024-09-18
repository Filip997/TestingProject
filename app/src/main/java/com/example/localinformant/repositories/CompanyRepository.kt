package com.example.localinformant.repositories

import com.example.localinformant.constants.AppConstants
import com.example.localinformant.models.Company
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CompanyRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun searchCompaniesByName(nameQuery: String): List<Company> =
        withContext(Dispatchers.IO) {
            try {
                val querySnapshot = db.collection(AppConstants.COMPANIES)
                    .orderBy("companyName")
                    .startAt(nameQuery)
                    .endAt(nameQuery + "\uf8ff")
                    .get()
                    .await()

                querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Company::class.java)
                }
            } catch (e: Exception) {
                listOf<Company>()
            }
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
}