package com.kl3jvi.animity.domain.repositories

import com.apollographql.apollo3.api.ApolloResponse
import com.kl3jvi.animity.SessionQuery
import com.kl3jvi.animity.ToggleFavouriteMutation
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val bearerToken: String?
    val refreshToken: String?
    val guestToken: String?
    val isAuthenticated: Boolean
    val isGuest: Boolean
    val userId: String?
    val expiration: Int?

    fun setBearerToken(authToken: String?)

    fun setRefreshToken(refreshToken: String?)

    fun setExpirationTime(expiration: Int)

    fun setAniListUserId(sync: String?)

    fun setProvider(provider: String)

    fun getSessionForUser(): Flow<ApolloResponse<SessionQuery.Data>>

    fun markAnimeAsFavorite(idAniList: Int?): Flow<ApolloResponse<ToggleFavouriteMutation.Data>>

    fun clearStorage(triggered: () -> Unit)
}
