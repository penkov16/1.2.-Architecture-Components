package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Token


interface  AuthRepository {

    suspend fun  loginUser(login: String, pass: String):Token

    suspend fun registerUser(login: String , name: String,pass: String):Token

}