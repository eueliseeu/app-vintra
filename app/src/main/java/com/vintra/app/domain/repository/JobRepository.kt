package com.vintra.app.domain.repository

import com.vintra.app.domain.model.Job
import com.vintra.app.domain.model.JobTag
import kotlinx.coroutines.flow.Flow

sealed interface CreateJobResult {
    data class Success(val jobId: String) : CreateJobResult
    data class Error(val message: String) : CreateJobResult
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

    fun observeJobs(limit: Long = 50): Flow<ObserveJobsResult>

    suspend fun getJobById(jobId: String): Job?
}