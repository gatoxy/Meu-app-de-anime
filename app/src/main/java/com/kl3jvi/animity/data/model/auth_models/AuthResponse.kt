package com.kl3jvi.animity.data.model.auth_models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("access_token")
    val accessToken: String? = "",
    @SerializedName("expires_in")
    val expiresIn: Int? = 1,
    @SerializedName("refresh_token")
    var refreshToken: String? = "",
    @SerializedName("token_type")
    val tokenType: String? = "",
)
