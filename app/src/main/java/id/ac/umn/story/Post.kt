package id.ac.umn.story

data class Post(
    val caption: String = "",
    val likes: Int = 0,
    val postId: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val likedBy: List<String> = emptyList()
)