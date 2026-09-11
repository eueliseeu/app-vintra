package com.vintra.app.domain.usecase.job

import com.vintra.app.domain.model.Job
import com.vintra.app.domain.repository.JobRepository
import javax.inject.Inject

class GetJobByIdUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    suspend operator fun invoke(jobId: String): Job? = jobRepository.getJobById(jobId)
}