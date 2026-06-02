package com.example.localinformant.conversations.data.repositories

import android.util.Log
import com.example.localinformant.conversations.domain.repositories.ConversationsRepository
import com.example.localinformant.core.data.constants.AppConstants
import com.example.localinformant.core.data.dto.CompanyDto
import com.example.localinformant.core.data.dto.ConversationDto
import com.example.localinformant.core.data.dto.MessageDto
import com.example.localinformant.core.data.dto.PersonDto
import com.example.localinformant.core.data.mappers.toDomain
import com.example.localinformant.core.domain.error.NetworkError
import com.example.localinformant.core.domain.models.Company
import com.example.localinformant.core.domain.models.Conversation
import com.example.localinformant.core.domain.models.Message
import com.example.localinformant.core.domain.models.Person
import com.example.localinformant.core.domain.models.User
import com.example.localinformant.core.domain.models.UserStatus
import com.example.localinformant.core.domain.network.NetworkChecker
import com.example.localinformant.core.domain.result.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ConversationsRepositoryImpl @Inject constructor(
    private val networkChecker: NetworkChecker,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ConversationsRepository {

    override suspend fun getUserConversations(): Result<List<Conversation>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!

            val conversations = db.collection(AppConstants.CONVERSATIONS)
                .whereArrayContains("participants", currentUserId)
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(ConversationDto::class.java)
                }
                .map {
                    it.toDomain(currentUserId)
                }
                .sortedBy { it.lastMessageTime }

            Result.Success(conversations)
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

    override suspend fun getUserById(userId: String): Result<User, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val personSnapshot = db.collection(AppConstants.PERSONS)
                .document(userId)
                .get()
                .await()

            if (personSnapshot.exists()) {
                val person = personSnapshot.toObject(PersonDto::class.java)?.toDomain()
                if (person != null) {
                    return Result.Success(person)
                }
            }

            val companySnapshot = db.collection(AppConstants.COMPANIES)
                .document(userId)
                .get()
                .await()

            if (companySnapshot.exists()) {
                val company = companySnapshot.toObject(CompanyDto::class.java)?.toDomain()
                if (company != null) {
                    return Result.Success(company)
                }
            }

            Result.Error(NetworkError.NOT_FOUND)
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

    override suspend fun observeUserStatus(userId: String): Flow<UserStatus> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(UserStatus.OFFLINE)
            close()
            return@callbackFlow
        }

        var personListener: ListenerRegistration? = null
        var companyListener: ListenerRegistration? = null

        personListener = db.collection(AppConstants.PERSONS)
            .whereEqualTo("id", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val person = snapshot.documents.firstOrNull()?.toObject(PersonDto::class.java)

                    if (person != null) {
                        val status = UserStatus.valueOf(person.status)
                        trySend(status)
                        return@addSnapshotListener
                    }
                }
            }

        companyListener = db.collection(AppConstants.COMPANIES)
            .whereEqualTo("id", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val company = snapshot.documents.firstOrNull()?.toObject(CompanyDto::class.java)

                    if (company != null) {
                        val status = UserStatus.valueOf(company.status)
                        trySend(status)
                        return@addSnapshotListener
                    }
                }
            }

        awaitClose {
            personListener.remove()
            companyListener.remove()
        }
    }

    private var lastDocument: DocumentSnapshot? = null
    private var newestMessageTime: Timestamp? = null

    override suspend fun loadMessagesByConversationId(
        conversationId: String,
        initial: Boolean
    ): Result<List<Message>, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            if (conversationId.isEmpty()) {
                return Result.Success(emptyList())
            }

            if (initial) {
                lastDocument = null
                newestMessageTime = null
            }

            var query = db.collection(AppConstants.MESSAGES)
                .whereEqualTo("conversationId", conversationId)
                .orderBy("timeSent", Query.Direction.DESCENDING)
                .limit(20)

            lastDocument?.let { lastSnapshot ->
                query = query.startAfter(lastSnapshot)
            }

            val snapshot = query.get().await()
            val messagesDto = snapshot.documents
                .mapNotNull { it.toObject(MessageDto::class.java) }

            Log.d("ConversationsRepository", "Messages: $messagesDto")

            if (initial) {
                newestMessageTime = messagesDto.firstOrNull()?.timeSent
            }

            if (snapshot.documents.isNotEmpty()) {
                lastDocument = snapshot.documents.last()
            }

            val messages = messagesDto.map { it.toDomain() }

            Result.Success(messages)
        } catch (e: FirebaseFirestoreException) {
            Log.d("ConversationsRepository", "Exception: ${e.message}")

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

    override suspend fun observeMessagesByConversationId(conversationId: String): Flow<List<Message>> =
        callbackFlow {
            if (conversationId.isEmpty()) {
                trySend(emptyList())
                close()
                return@callbackFlow
            }

            val listener = db.collection(AppConstants.MESSAGES)
                .whereEqualTo("conversationId", conversationId)
                .orderBy("timeSent", Query.Direction.ASCENDING)
                .startAfter(newestMessageTime)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val messages = snapshot.documents
                            .mapNotNull { it.toObject(MessageDto::class.java) }
                            .map { it.toDomain() }
                            .sortedByDescending { it.timeSent }

                        trySend(messages)
                    }
                }

            awaitClose {
                listener.remove()
            }
        }

    override suspend fun sendMessage(
        conversationId: String,
        receiverId: String,
        messageText: String
    ): Result<Unit, NetworkError> {
        return try {
            if (!networkChecker.hasInternetConnection()) {
                return Result.Error(NetworkError.NO_INTERNET_CONNECTION)
            }

            val currentUserId = auth.currentUser?.uid!!
            val messageId = UUID.randomUUID().toString()
            val timeSent = Timestamp.now()

            val messageDto = MessageDto(
                id = messageId,
                conversationId = conversationId,
                senderId = currentUserId,
                receiverId = receiverId,
                content = messageText,
                timeSent = timeSent
            )

            db.collection(AppConstants.MESSAGES)
                .document(messageId)
                .set(messageDto)
                .await()

            db.collection(AppConstants.CONVERSATIONS)
                .document(conversationId)
                .update("messages", FieldValue.arrayUnion(messageId))
                .await()

            db.collection(AppConstants.CONVERSATIONS)
                .document(conversationId)
                .update("lastMessage", messageText)
                .await()

            db.collection(AppConstants.CONVERSATIONS)
                .document(conversationId)
                .update("lastMessageUserId", currentUserId)
                .await()

            db.collection(AppConstants.CONVERSATIONS)
                .document(conversationId)
                .update("lastMessageTime", timeSent)
                .await()

            Result.Success(Unit)
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