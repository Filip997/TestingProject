package com.example.localinformant.repositories

import com.example.localinformant.constants.AppConstants
import com.example.localinformant.models.Person
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PersonRepository {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun searchPersonsByName(nameQuery: String): List<Person> =
        withContext(Dispatchers.IO) {
            try {
                val querySnapshot = db.collection(AppConstants.PERSONS)
                    .orderBy("name")
                    .startAt(nameQuery)
                    .endAt(nameQuery + "\uf8ff")
                    .get()
                    .await()

                querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Person::class.java)
                }
            } catch (e: Exception) {
                listOf<Person>()
            }
        }

    suspend fun getCurrentPerson(): Person? =
        withContext(Dispatchers.IO) {
            try {
                val documentSnapshot = db.collection(AppConstants.PERSONS)
                    .document(auth.currentUser?.uid!!)
                    .get()
                    .await()

                documentSnapshot.toObject(Person::class.java)
            } catch (e: Exception) {
                null
            }
        }
}