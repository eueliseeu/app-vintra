package com.vintra.app.ui.jobs

import com.vintra.app.domain.model.Job

data class JobsUiState(
    val isLoading: Boolean = true,
    val jobs: List<Job> = emptyList(),
    val searchQuery: String = "",
    val currentUid: String? = null,
    val selectedFeedTab: JobFeedTab = JobFeedTab.RECOMMENDED,
    val errorMessage: String? = null
) {
    val visibleJobs: List<Job>
        get() {
            val byTab = when (selectedFeedTab) {
                JobFeedTab.RECOMMENDED -> jobs
                JobFeedTab.MY_PUBLIC -> jobs.filter { job ->
                    currentUid != null && job.publisherUid == currentUid
                }
            }
            val q = searchQuery.trim().lowercase()
            if (q.isBlank()) return byTab
            return byTab.filter { job ->
                job.title.lowercase().contains(q) ||
                        job.companyName.lowercase().contains(q) ||
                        job.companyUsername.lowercase().contains(q) ||
                        job.description.lowercase().contains(q) ||
                        job.tags.any { it.label.lowercase().contains(q) }
            }
        }
}