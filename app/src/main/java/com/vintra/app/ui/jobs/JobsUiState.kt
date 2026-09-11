package com.vintra.app.ui.jobs

import com.vintra.app.domain.model.Job

data class JobsUiState(
    val isLoading: Boolean = true,
    val jobs: List<Job> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
) {
    val filteredJobs: List<Job>
        get() {
            val q = searchQuery.trim().lowercase()
            if (q.isBlank()) return jobs
            return jobs.filter { job ->
                job.title.lowercase().contains(q) ||
                        job.companyName.lowercase().contains(q) ||
                        job.companyUsername.lowercase().contains(q) ||
                        job.description.lowercase().contains(q) ||
                        job.tags.any { it.label.lowercase().contains(q) }
            }
        }
}