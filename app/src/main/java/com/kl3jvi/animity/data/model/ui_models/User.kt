package com.kl3jvi.animity.data.model.ui_models

import android.os.Parcelable
import com.kl3jvi.animity.type.Favourites
import com.kl3jvi.animity.type.MediaListOptions
import com.kl3jvi.animity.type.ModRole
import com.kl3jvi.animity.type.UserStatisticTypes
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class User(
    val id: Int = 0,
    val name: String = "",
    val about: String = "",
    val avatar: UserAvatar = UserAvatar(),
    val bannerImage: String = "",
    var isFollowing: Boolean = false,
    val isFollower: Boolean = false,
    val isBlocked: Boolean = false,
    val mediaListOptions: @RawValue MediaListOptions = MediaListOptions(),
    val favourites: @RawValue Favourites = Favourites(),
    val statistics: @RawValue UserStatisticTypes = UserStatisticTypes(),
    val unreadNotificationCount: Int = 0,
    val siteUrl: String = "",
    val donatorTier: Int = 0,
    val donatorBadge: String = "",
    val moderatorRoles: List<ModRole> = listOf()
) : Parcelable {
    override fun toString(): String {
        return name
    }
}
