package com.tg.electroaires.model

data class Token(
    val username: String,
    val password: String
)

data class ResponseToken(
    val refresh: String,
    val access: String
)

data class TokenRefresh(
    val refresh: String
)

data class ResponseTokenRefresh(
    val access: String
)


