package com.kl3jvi.animity.domain.repositories

import com.kl3jvi.animity.data.model.ui_models.HomeData
import com.kl3jvi.animity.data.model.ui_models.Keys
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getHomeData(): Flow<HomeData>

    fun getEncryptionKeys(): Flow<Keys>
}
