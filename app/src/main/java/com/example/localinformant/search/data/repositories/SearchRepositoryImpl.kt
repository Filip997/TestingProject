package com.example.localinformant.search.data.repositories

import com.example.localinformant.constants.AppConstants
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.mappers.toDomain
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.core.domain.result.Result
import com.example.localinformant.search.domain.repositories.SearchRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val networkChecker: NetworkChecker
) : SearchRepository {

    override suspend fun searchPersonsByName(searchQuery: String): Result<List<Person>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            if (searchQuery.isEmpty()) {
                return Result.Success(emptyList())
            }

            val lowercaseQuery = searchQuery.lowercase()
            val allPersons = mutableListOf<Person>()

            val querySnapshotByFirstName = db.collection(AppConstants.PERSONS)
                .orderBy("firstNameLowerCase")
                .whereGreaterThanOrEqualTo("firstNameLowerCase", lowercaseQuery)
                .whereLessThanOrEqualTo("firstNameLowerCase", "$lowercaseQuery\uf8ff")
                .get()
                .await()

            val personsByFirstName = querySnapshotByFirstName
                .documents
                .mapNotNull {
                    it.toObject(PersonDto::class.java)
                }
                .filter { person ->
                    person.firstNameLowerCase.startsWith(lowercaseQuery, ignoreCase = true)
                }
                .map {
                    it.toDomain()
                }

            if (personsByFirstName.isNotEmpty()) {
                allPersons.addAll(personsByFirstName)
            }

            val querySnapshotByLastName = db.collection(AppConstants.PERSONS)
                .orderBy("lastNameLowerCase")
                .whereGreaterThanOrEqualTo("lastNameLowerCase", lowercaseQuery)
                .whereLessThanOrEqualTo("lastNameLowerCase", "$lowercaseQuery\uf8ff")
                .get()
                .await()

            val personsByLastName = querySnapshotByLastName
                .documents
                .mapNotNull {
                    it.toObject(PersonDto::class.java)
                }
                .filter { person ->
                    person.lastNameLowerCase.startsWith(lowercaseQuery, ignoreCase = true)
                }
                .map {
                    it.toDomain()
                }

            if (personsByLastName.isNotEmpty()) {
                allPersons.addAll(personsByLastName)
            }

            Result.Success(allPersons.distinctBy { it.id })
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> Result.Error(NetworkError.INVALID_ARGUMENT)
                FirebaseFirestoreException.Code.NOT_FOUND -> Result.Error(NetworkError.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Result.Error(NetworkError.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> Result.Error(NetworkError.RESOURCE_EXHAUSTED)
                FirebaseFirestoreException.Code.ABORTED -> Result.Error(NetworkError.ABORTED)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Result.Error(NetworkError.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.UNKNOWN -> Result.Error(NetworkError.UNKNOWN)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun searchCompaniesByName(searchQuery: String): Result<List<Company>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            if (searchQuery.isEmpty()) {
                return Result.Success(emptyList())
            }

            val lowercaseQuery = searchQuery.lowercase()

            val querySnapshot = db.collection(AppConstants.COMPANIES)
                .orderBy("companyNameLowerCase")
                .whereGreaterThanOrEqualTo("companyNameLowerCase", lowercaseQuery)
                .whereLessThanOrEqualTo("companyNameLowerCase", "$lowercaseQuery\uf8ff")
                .get()
                .await()

            val companies = querySnapshot
                .documents
                .mapNotNull {
                    it.toObject(CompanyDto::class.java)
                }
                .filter { company ->
                    company.companyNameLowerCase.startsWith(lowercaseQuery, ignoreCase = true)
                }
                .map {
                    it.toDomain()
                }

            Result.Success(companies)
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.INVALID_ARGUMENT -> Result.Error(NetworkError.INVALID_ARGUMENT)
                FirebaseFirestoreException.Code.NOT_FOUND -> Result.Error(NetworkError.NOT_FOUND)
                FirebaseFirestoreException.Code.ALREADY_EXISTS -> Result.Error(NetworkError.ALREADY_EXISTS)
                FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> Result.Error(NetworkError.RESOURCE_EXHAUSTED)
                FirebaseFirestoreException.Code.ABORTED -> Result.Error(NetworkError.ABORTED)
                FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> Result.Error(NetworkError.DEADLINE_EXCEEDED)
                FirebaseFirestoreException.Code.UNKNOWN -> Result.Error(NetworkError.UNKNOWN)
                else -> Result.Error(NetworkError.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(NetworkError.UNKNOWN)
        }
    }
}