package com.vintra.app.domain.usecase.job

import com.vintra.app.domain.repository.DeleteJobResult
import com.vintra.app.domain.repository.JobRepository
import javax.inject.Inject

class DeleteJobUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend operator fun invoke(jobId: String, requesterUid: String): DeleteJobResult =
        jobRepository.deleteJob(jobId, requesterUid)
}