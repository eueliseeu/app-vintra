package com.vintra.app.domain.usecase.job

import com.vintra.app.domain.repository.JobRepository
import com.vintra.app.domain.repository.ObserveJobsResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveJobsUseCase @Inject constructor(
    private val jobRepository: JobRepository
) {
    operator fun invoke(limit: Long = 50): Flow<ObserveJobsResult> =
        jobRepository.observeJobs(limit)
}