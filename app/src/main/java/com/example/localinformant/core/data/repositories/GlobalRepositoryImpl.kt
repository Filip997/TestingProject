package com.example.localinformant.core.data.repositories

import android.util.Log
import com.example.localinformant.constants.AppConstants
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.mappers.toDomain
import com.example.localinformant.core.domain.repositories.GlobalRepository
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GlobalRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : GlobalRepository {

    override suspend fun updatePersonToken(token: String): Person? {
        return try {
            val currentUserId = auth.currentUser?.uid!!
            db.collection(AppConstants.PERSONS)
                .document(currentUserId)
                .update("token", token)
                .await()

            getPersonById(currentUserId)?.toDomain()
        } catch (e: Exception) {
            Log.d("GlobalRepository", "Update token for person failed: ${e.toString()}")

            null
        }
    }

    override suspend fun updateCompanyToken(token: String): Company? {
        return try {
            val currentUserId = auth.currentUser?.uid!!
            db.collection(AppConstants.COMPANIES)
                .document(currentUserId)
                .update("token", token)
                .await()

            getCompanyById(currentUserId)?.toDomain()
        } catch (e: Exception) {
            Log.d("GlobalRepository", "Update token for company failed: ${e.toString()}")

            null
        }
    }

    private suspend fun getPersonById(id: String): PersonDto? {
        return db.collection(AppConstants.PERSONS)
            .document(id)
            .get()
            .await()
            .toObject(PersonDto::class.java)
    }

    private suspend fun getCompanyById(id: String): CompanyDto? {
        return db.collection(AppConstants.COMPANIES)
            .document(id)
            .get()
            .await()
            .toObject(CompanyDto::class.java)
    }
}