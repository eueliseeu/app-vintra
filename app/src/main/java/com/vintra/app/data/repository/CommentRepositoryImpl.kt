package com.vintra.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vintra.app.data.mapper.toDomain
import com.vintra.app.data.model.CommentDto
import com.vintra.app.data.model.UserProfileDto
import com.vintra.app.domain.model.Comment
import com.vintra.app.domain.repository.CommentRepository
import com.vintra.app.domain.repository.CreateCommentResult
import com.vintra.app.domain.repository.ObserveCommentsResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_COMMENTS = "comments"
private const val COLLECTION_POSTS = "posts"
private const val COLLECTION_USERS = "users"

class CommentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CommentRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override suspend fun createComment(
        postId: String,
        authorUid: String,
        authorName: String,
        authorUsername: String,
        authorPhotoBase64: String?,
        text: String,
        isVerified: Boolean
    ): CreateCommentResult = try {
        val commentRef = firestore.collection(COLLECTION_COMMENTS).document()
        val postRef = firestore.collection(COLLECTION_POSTS).document(postId)

        val commentData = hashMapOf<String, Any?>(
            "postId" to postId,
            "authorUid" to authorUid,
            "authorName" to authorName,
            "authorUsername" to authorUsername,
            "authorPhotoBase64" to authorPhotoBase64,
            "text" to text.trim(),
            "createdAt" to System.currentTimeMillis(),
            "isVerified" to isVerified
        )

        firestore.runTransaction { transaction ->
            val postSnapshot = transaction.get(postRef)
            if (!postSnapshot.exists()) {
                throw Exception("Post não encontrado.")
            }

            val currentCount = postSnapshot.getLong("commentCount") ?: 0L

            transaction.set(commentRef, commentData)
            transaction.update(postRef, "commentCount", currentCount + 1)
        }.await()

        CreateCommentResult.Success(commentRef.id)
    } catch (exception: Exception) {
        CreateCommentResult.Error(exception.message ?: "Erro ao comentar.")
    }

    override fun observeComments(postId: String, limit: Long): Flow<ObserveCommentsResult> = callbackFlow {
        if (postId.isBlank()) {
            trySend(ObserveCommentsResult.Error("postId inválido."))
            close()
            return@callbackFlow
        }

        val registration = firestore.collection(COLLECTION_COMMENTS)
            .whereEqualTo("postId", postId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(
                        ObserveCommentsResult.Error(
                            error.message ?: "Erro ao carregar comentários."
                        )
                    )
                    return@addSnapshotListener
                }

                val comments = snapshot?.documents.orEmpty().mapNotNull { document ->
                    document.toObject(CommentDto::class.java)?.toDomain(document.id)
                }

                // Enriquece com isVerified ao vivo do perfil do autor
                scope.launch {
                    val enriched = enrichCommentsWithVerification(comments)
                    trySend(ObserveCommentsResult.Success(enriched))
                }
            }

        awaitClose { registration.remove() }
    }

    private suspend fun enrichCommentsWithVerification(comments: List<Comment>): List<Comment> {
        if (comments.isEmpty()) return comments

        val uids = comments.map { it.authorUid }.distinct().filter { it.isNotBlank() }
        if (uids.isEmpty()) return comments

        val verifiedMap = mutableMapOf<String, Boolean>()

        uids.forEach { uid ->
            try {
                val snap = firestore.collection(COLLECTION_USERS).document(uid).get().await()
                verifiedMap[uid] = snap.toObject(UserProfileDto::class.java)?.isVerified == true
            } catch (_: Exception) {
                verifiedMap[uid] = false
            }
        }

        return comments.map { comment ->
            comment.copy(isVerified = verifiedMap[comment.authorUid] == true)
        }
    }
}