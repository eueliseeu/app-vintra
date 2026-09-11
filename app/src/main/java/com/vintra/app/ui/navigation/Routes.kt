package com.vintra.app.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data object SessionRouterRoute

@Serializable
data object ProfileSetupRoute

@Serializable
data object HomeRoute

@Serializable
data object JobsRoute

@Serializable
data class PostDetailRoute(val postId: String)

@Serializable
data class JobDetailRoute(val jobId: String)

@Serializable
data object CreatePostRoute

@Serializable
data object CreateJobRoute