package com.example.localinformant.repositories

import android.util.Log
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

    /*suspend fun searchPersonsByName(): List<Person> =
        withContext(Dispatchers.IO) {
            try {
                val querySnapshot = db.collection(AppConstants.PERSONS)
                    .orderBy("fullName")
                    .get()
                    .await()

                *//*querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(Person::class.java)
                }*//*
            } catch (e: Exception) {
                listOf<Person>()
            }
        }*/

    suspend fun searchPersonsByName(): List<Person> {
        return db.collection(AppConstants.PERSONS)
            .orderBy("fullName")
            .get()
            .await()
            .toObjects(Person::class.java)
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

    suspend fun getPersonById(id: String): Person? =
        withContext(Dispatchers.IO) {
            try {
                val documentSnapshot = db.collection(AppConstants.PERSONS)
                    .document(id)
                    .get()
                    .await()

                documentSnapshot.toObject(Person::class.java)
            } catch (e: Exception) {
                null
            }
        }

    suspend fun updatePersonToken(token: String) {
        withContext(Dispatchers.IO) {
            try {
                db.collection(AppConstants.PERSONS).document(auth.currentUser?.uid!!).update("token", token)
            } catch (e: Exception) {
                Log.d("updatePersonToken", e.printStackTrace().toString())
            }
        }
    }
}