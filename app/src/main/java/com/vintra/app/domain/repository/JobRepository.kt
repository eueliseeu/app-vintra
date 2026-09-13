package com.vintra.app.domain.repository

import com.vintra.app.domain.model.Job
import com.vintra.app.domain.model.JobTag
import kotlinx.coroutines.flow.Flow

sealed interface CreateJobResult {
    data class Success(val jobId: String) : CreateJobResult
    data class Error(val message: String) : CreateJobResult
}

sealed interface UpdateJobResult {
    data object Success : UpdateJobResult
    data class Error(val message: String) : UpdateJobResult
}

sealed interface DeleteJobResult {
    data object Success : DeleteJobResult
    data class Error(val message: String) : DeleteJobResult
}

sealed interface ObserveJobsResult {
    data class Success(val jobs: List<Job>) : ObserveJobsResult
    data class Error(val message: String) : ObserveJobsResult
}

interface JobRepository {
    suspend fun createJob(
        publisherUid: String,
        companyName: String,
        companyUsername: String,
        companyPhotoBase64: String?,
        title: String,
        description: String,
        linkUrl: String,
        buttonLabel: String,
        tags: List<JobTag>
    ): CreateJobResult

    suspend fun updateJob(
        jobId: String,
        publisherUid: String,
        companyPhotoBase64: String?,
        title: String,
        description: String,
        linkUrl: String,
        buttonLabel: String,
        tags: List<JobTag>
    ): UpdateJobResult

    suspend fun deleteJob(jobId: String, requesterUid: String): DeleteJobResult

    fun observeJobs(limit: Long = 50): Flow<ObserveJobsResult>

    suspend fun getJobById(jobId: String): Job?
}