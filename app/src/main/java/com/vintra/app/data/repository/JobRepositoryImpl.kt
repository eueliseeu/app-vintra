package com.vintra.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vintra.app.data.mapper.toDomain
import com.vintra.app.data.model.JobDto
import com.vintra.app.domain.model.Job
import com.vintra.app.domain.model.JobTag
import com.vintra.app.domain.repository.CreateJobResult
import com.vintra.app.domain.repository.JobRepository
import com.vintra.app.domain.repository.ObserveJobsResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION_JOBS = "jobs"

class JobRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : JobRepository {

    override suspend fun createJob(
        publisherUid: String,
        companyName: String,
        companyUsername: String,
        companyPhotoBase64: String?,
        title: String,
        description: String,
        linkUrl: String,
        buttonLabel: String,
        tags: List<JobTag>
    ): CreateJobResult = try {
        val dto = JobDto(
            publisherUid = publisherUid,
            companyName = companyName,
            companyUsername = companyUsername,
            companyPhotoBase64 = companyPhotoBase64,
            title = title,
            description = description,
            linkUrl = linkUrl,
            buttonLabel = buttonLabel,
            tags = tags.map { it.name },
            createdAt = System.currentTimeMillis()
        )
        val docRef = firestore.collection(COLLECTION_JOBS).add(dto).await()
        CreateJobResult.Success(docRef.id)
    } catch (exception: Exception) {
        CreateJobResult.Error(exception.message ?: "Error publishing job.")
    }

    override fun observeJobs(limit: Long): Flow<ObserveJobsResult> = callbackFlow {
        val registration = firestore.collection(COLLECTION_JOBS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ObserveJobsResult.Error(error.message ?: "Error observing jobs."))
                    return@addSnapshotListener
                }
                val jobs = snapshot?.documents.orEmpty().mapNotNull { document ->
                    document.toObject(JobDto::class.java)?.toDomain(document.id)
                }
                trySend(ObserveJobsResult.Success(jobs))
            }
        awaitClose { registration.remove() }
    }

    override suspend fun getJobById(jobId: String): Job? = try {
        val document = firestore.collection(COLLECTION_JOBS).document(jobId).get().await()
        if (!document.exists()) null
        else document.toObject(JobDto::class.java)?.toDomain(document.id)
    } catch (_: Exception) {
        null
    }
}