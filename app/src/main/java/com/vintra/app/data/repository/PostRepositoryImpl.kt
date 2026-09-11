package com.vintra.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vintra.app.data.mapper.toDomain
import com.vintra.app.data.model.PostDto
import com.vintra.app.data.model.UserProfileDto
import com.vintra.app.domain.model.AuthProvider
import com.vintra.app.domain.model.Post
import com.vintra.app.domain.repository.CreatePostResult
import com.vintra.app.domain.repository.ObservePostsResult
import com.vintra.app.domain.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_POSTS = "posts"
private const val COLLECTION_USERS = "users"

class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override suspend fun createPost(
        authorUid: String,
        authorName: String,
        authorUsername: String,
        authorPhotoBase64: String?,
        authorProvider: AuthProvider,
        title: String,
        text: String,
        linkUrl: String,
        imageBase64: String?,
        isVerified: Boolean
    ): CreatePostResult = try {
        val dto = PostDto(
            authorUid = authorUid,
            authorName = authorName,
            authorUsername = authorUsername,
            authorPhotoBase64 = authorPhotoBase64,
            authorProvider = authorProvider.name,
            title = title,
            text = text,
            linkUrl = linkUrl,
            imageBase64 = imageBase64,
            createdAt = System.currentTimeMillis(),
            commentCount = 0,
            isVerified = isVerified
        )
        val docRef = firestore.collection(COLLECTION_POSTS).add(dto).await()
        CreatePostResult.Success(docRef.id)
    } catch (exception: Exception) {
        CreatePostResult.Error(exception.message ?: "Erro ao publicar post.")
    }

    override fun observeFeed(limit: Long): Flow<ObservePostsResult> = callbackFlow {
        val registration = firestore.collection(COLLECTION_POSTS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ObservePostsResult.Error(error.message ?: "Erro ao observar feed."))
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents.orEmpty().mapNotNull { document ->
                    document.toObject(PostDto::class.java)?.toDomain(document.id)
                }

                scope.launch {
                    val enriched = enrichPostsWithVerification(posts)
                    trySend(ObservePostsResult.Success(enriched))
                }
            }

        awaitClose { registration.remove() }
    }

    override suspend fun getPostById(postId: String): Post? = try {
        val document = firestore.collection(COLLECTION_POSTS)
            .document(postId)
            .get()
            .await()

        if (!document.exists()) null
        else {
            val post = document.toObject(PostDto::class.java)?.toDomain(document.id)
            post?.let { enrichPostsWithVerification(listOf(it)).first() }
        }
    } catch (exception: Exception) {
        null
    }

    private suspend fun enrichPostsWithVerification(posts: List<Post>): List<Post> {
        if (posts.isEmpty()) return posts

        val uids = posts.map { it.authorUid }.distinct().filter { it.isNotBlank() }
        if (uids.isEmpty()) return posts

        val verifiedMap = mutableMapOf<String, Boolean>()

        uids.forEach { uid ->
            try {
                val snap = firestore.collection(COLLECTION_USERS).document(uid).get().await()
                val verified = snap.toObject(UserProfileDto::class.java)?.isVerified == true
                verifiedMap[uid] = verified
            } catch (_: Exception) {
                verifiedMap[uid] = false
            }
        }

        return posts.map { post ->
            post.copy(isVerified = verifiedMap[post.authorUid] == true)
        }
    }
}