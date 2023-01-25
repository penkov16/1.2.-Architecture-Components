package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val author :String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean,
    val likes: Int = 0,
    val newer: Long,
    val  authorId: Long,
    val attachment: Attachment? = null,
    var ownedByMe: Boolean = false
)

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
