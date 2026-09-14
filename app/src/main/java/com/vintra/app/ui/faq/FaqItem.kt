package com.vintra.app.ui.faq

data class FaqItem(
    val id: String,
    val question: String,
    val answer: String
)

val VintraFaqItems: List<FaqItem> = listOf(
    FaqItem(
        id = "1",
        question = "What is Vintra?",
        answer = "Vintra is a social platform focused on developers and the tech community. Share posts, discover jobs and connect with verified professionals."
    ),
    FaqItem(
        id = "3",
        question = "How do I get a verified badge?",
        answer = "Verification is managed by Vintra. Verified accounts can create job posts and display the verified badge next to their name."
    ),
    FaqItem(
        id = "4",
        question = "Can I edit or delete my posts?",
        answer = "Yes. Open your post, tap the menu (?) and choose Edit or Delete. Only the author can edit or delete their own posts."
    ),
    FaqItem(
        id = "5",
        question = "Who can delete a comment?",
        answer = "The author of the comment can edit or delete it. The owner of the post can also delete comments on their post."
    ),
    FaqItem(
        id = "6",
        question = "How do job tags work?",
        answer = "When creating a job you can select tags such as CLT, Freelancer, PJ, Trainee or Internship. They help others filter opportunities."
    ),
    FaqItem(
        id = "7",
        question = "Is my profile public?",
        answer = "Your name, username, photo and posts are visible to signed-in users on the platform according to Vintra's feed and profile rules."
    )
)