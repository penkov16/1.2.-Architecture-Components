package ru.netology.nmedia.dto

import ru.netology.nmedia.auth.AuthState

data class Token(
    val id : Long,
    val token: String,
    val avatar: String? = null,
)