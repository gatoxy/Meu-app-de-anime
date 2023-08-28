package com.kl3jvi.animity.data.model.ui_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EpisodeInfo(
    var vidCdnUrl: String? = null,
    var nextEpisodeUrl: String? = null,
    var previousEpisodeUrl: String? = null,
) : Parcelable
